import java.io.*;
import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;
public class MiniBank {
    record BankInfo(String name, String branch) {}
    enum MenuOption { OPEN_ACCOUNT, DEPOSIT, WITHDRAW, TRANSFER, STATEMENT, LIST, REPORT, EXIT }
    enum TransactionType { DEPOSIT, WITHDRAW, TRANSFER }

    @Retention(RetentionPolicy.RUNTIME) @Target(ElementType.FIELD)
    @interface Id {}
    @Retention(RetentionPolicy.RUNTIME) @Target(ElementType.FIELD)
    @interface Positive { String message() default "must be > 0"; }
    @Retention(RetentionPolicy.RUNTIME) @Target(ElementType.FIELD)
    @interface MaxLength { int value(); }
    @Retention(RetentionPolicy.RUNTIME) @Target(ElementType.METHOD)
    @interface Audit {}

    static class BankException extends Exception {
        BankException(String message) { super(message); }
    }
    static class InsufficientFundsException extends BankException {
        final long shortfall;
        InsufficientFundsException(long shortfall) {
            super("Insufficient funds; short by " + shortfall);
            this.shortfall = shortfall;
        }
    }
    static class AccountNotFoundException extends BankException {
        AccountNotFoundException(String id) { super("Account not found: " + id); }
    }
    static class InvalidAmountException extends BankException {
        InvalidAmountException() { super("Amount must be greater than zero"); }
    }

    static class Customer implements Serializable, Cloneable {
        private static final long serialVersionUID = 1L;
        private static long counter = 100;
        private final String customerId;
        @MaxLength(60) private String name;
        private String email, mobile;
        private Address address;
        static class Address implements Serializable {
            private static final long serialVersionUID = 1L;
            private final String line, city, pincode;
            Address(String line, String city, String pincode) {
                this.line=line; this.city=city; this.pincode=pincode;
            }
            public String getLine(){return line;}
            public String getCity(){return city;}
            public String getPincode(){return pincode;}
        }
        Customer(String name, String email, String mobile, Address address) {
            this.customerId = "CUST" + (++counter);
            this.name=name; this.email=email; this.mobile=mobile; this.address=address;
        }
        public String getCustomerId(){return customerId;}
        public String getName(){return name;}
        public String getEmail(){return email;}
        public String getMobile(){return mobile;}
        public Address getAddress(){return address;}
        @Override public Customer clone() {
            try { return (Customer) super.clone(); }
            catch (CloneNotSupportedException e) { throw new AssertionError(e); }
        }
    }

    static abstract class Account implements Serializable, Comparable<Account> {
        private static final long serialVersionUID = 1L;
        private static long counter = 0;
        @Id private final String accountNumber;
        private String ownerName;
        @Positive private long balance;
        private boolean active = true;
        protected Account(String ownerName, long openingBalance) {
            this.accountNumber = String.format("AC%04d", ++counter);
            this.ownerName=ownerName; this.balance=openingBalance;
        }
        public String getAccountNumber(){return accountNumber;}
        public String getOwnerName(){return ownerName;}
        public synchronized long getBalance(){return balance;}
        public boolean isActive(){return active;}
        public synchronized void deposit(long amount) throws InvalidAmountException {
            if(amount<=0) throw new InvalidAmountException();
            balance += amount;
        }
        public synchronized boolean withdraw(long amount) throws InsufficientFundsException, InvalidAmountException {
            if(amount<=0) throw new InvalidAmountException();
            if(!canWithdraw(amount)) throw new InsufficientFundsException(amount - Math.max(0,balance));
            balance -= amount; return true;
        }
        protected abstract double interestRate();
        protected abstract boolean canWithdraw(long amount);
        synchronized void creditFromTransfer(long amount) throws InvalidAmountException { deposit(amount); }
        synchronized void debitFromTransfer(long amount) throws BankException { withdraw(amount); }
        @Override public int compareTo(Account other){return accountNumber.compareTo(other.accountNumber);}
        @Override public String toString(){return accountNumber+" | "+ownerName+" | balance="+balance+" | "+getClass().getSimpleName();}
        @Override public boolean equals(Object o){return o instanceof Account a && accountNumber.equals(a.accountNumber);}
        @Override public int hashCode(){return accountNumber.hashCode();}
    }
    static class SavingsAccount extends Account {
        private static final long serialVersionUID=1L; private final long minBalance;
        SavingsAccount(String name,long opening,long minBalance){super(name,opening);this.minBalance=minBalance;}
        @Override protected double interestRate(){return 4.0;}
        @Override protected boolean canWithdraw(long amount){return getBalance()-amount>=minBalance;}
    }
    static class CurrentAccount extends Account {
        private static final long serialVersionUID=1L; private final long overdraftLimit;
        CurrentAccount(String name,long opening,long limit){super(name,opening);this.overdraftLimit=limit;}
        @Override protected double interestRate(){return 0.0;}
        @Override protected boolean canWithdraw(long amount){return getBalance()-amount>=-overdraftLimit;}
    }
    static class FixedDepositAccount extends Account {
        private static final long serialVersionUID=1L;
        FixedDepositAccount(String name,long opening){super(name,opening);}
        @Override protected double interestRate(){return 7.0;}
        @Override protected boolean canWithdraw(long amount){return false;}
    }

    static class Validator {
        private static final Pattern MOBILE=Pattern.compile("^[6-9][0-9]{9}$");
        private static final Pattern EMAIL=Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        private static final Pattern PAN=Pattern.compile("^[A-Z]{5}[0-9]{4}[A-Z]$");
        private static final Pattern IFSC=Pattern.compile("^[A-Z]{4}0[A-Z0-9]{6}$");
        public static boolean isValidMobile(String s){return s!=null&&MOBILE.matcher(s).matches();}
        public static boolean isValidEmail(String s){return s!=null&&EMAIL.matcher(s).matches();}
        public static boolean isValidPan(String s){return s!=null&&PAN.matcher(s).matches();}
        public static boolean isValidIfsc(String s){return s!=null&&IFSC.matcher(s).matches();}
        public static boolean isValidAmount(String s){return s!=null&&s.matches("[1-9][0-9]*");}
    }
    record Command(TransactionType type, String accountNumber, long amount) {}
    static class CommandParser {
        static Command parse(String line) {
            String[] p=line.trim().split("\\s+");
            if(p.length!=3) throw new IllegalArgumentException("Use: TYPE ACCOUNT_NUMBER AMOUNT");
            return new Command(TransactionType.valueOf(p[0].toUpperCase()),p[1],Long.parseLong(p[2]));
        }
    }
    static class StatementFormatter {
        static String buildStatement(Account a) {
            return "----- MINI BANK STATEMENT -----\nAccount: "+a.getAccountNumber()+
                "\nOwner: "+a.getOwnerName()+"\nBalance: ₹"+a.getBalance()+
                "\nAccount type: "+a.getClass().getSimpleName()+"\nInterest rate: "+a.interestRate()+"%\n";
        }
    }
    static class AnnotationValidator {
        static String[] validate(Object obj) {
            List<String> errors=new ArrayList<>();
            for(Class<?> c=obj.getClass(); c!=null; c=c.getSuperclass()) for(Field f:c.getDeclaredFields()) {
                try {
                    f.setAccessible(true); Object v=f.get(obj);
                    if(f.isAnnotationPresent(Positive.class) && v instanceof Number n && n.longValue()<=0)
                        errors.add(f.getName()+": "+f.getAnnotation(Positive.class).message());
                    if(f.isAnnotationPresent(MaxLength.class) && v instanceof String s && s.length()>f.getAnnotation(MaxLength.class).value())
                        errors.add(f.getName()+": maximum length is "+f.getAnnotation(MaxLength.class).value());
                } catch (ReflectiveOperationException e) { errors.add("Cannot validate "+f.getName()); }
            }
            return errors.toArray(String[]::new);
        }
    }
    static class Repository<T> {
        private final Map<String,T> data=new ConcurrentHashMap<>();
        private String idOf(T entity) {
            for(Class<?> c=entity.getClass();c!=null;c=c.getSuperclass()) for(Field f:c.getDeclaredFields()) {
                if(f.isAnnotationPresent(Id.class)) try {f.setAccessible(true);return String.valueOf(f.get(entity));}
                catch(ReflectiveOperationException e){throw new IllegalArgumentException("Cannot read @Id",e);}
            }
            throw new IllegalArgumentException("No field marked @Id in "+entity.getClass().getSimpleName());
        }
        T save(T entity){data.put(idOf(entity),entity);return entity;}
        @SafeVarargs final void saveAll(T... items){for(T item:items)save(item);}
        T findById(String id){return data.get(id);}
        ArrayList<T> findAll(){return new ArrayList<>(data.values());}
        boolean delete(String id){return data.remove(id)!=null;}
        int size(){return data.size();}
    }
    static class AccountStore {
        private final Map<String,Account> accounts=new ConcurrentHashMap<>();
        private final ArrayList<String> history=new ArrayList<>();
        void add(Account a){accounts.put(a.getAccountNumber(),a);}
        Account get(String id){return accounts.get(id);}
        ArrayList<Account> getAll(){return new ArrayList<>(accounts.values());}
        ArrayList<Account> sortedByBalance(){
            ArrayList<Account> result=getAll();
            result.sort(Comparator.comparingLong(Account::getBalance).reversed()); return result;
        }
        synchronized void addHistory(String s){history.add(s);}
        synchronized ArrayList<String> getHistory(){return new ArrayList<>(history);}
        Map<String,Account> concurrentAccounts(){return accounts;}
    }
    static class StatePersister {
        static void save(Account[] accounts, Path file) throws IOException {
            if(file.getParent()!=null) Files.createDirectories(file.getParent());
            try(ObjectOutputStream out=new ObjectOutputStream(Files.newOutputStream(file))){out.writeObject(accounts);}
        }
        static Account[] load(Path file) throws IOException,ClassNotFoundException {
            if(!Files.exists(file))return new Account[0];
            try(ObjectInputStream in=new ObjectInputStream(Files.newInputStream(file))){return (Account[])in.readObject();}
        }
    }
    static class TransactionLog {
        static void append(Path file,String line)throws IOException{
            Files.createDirectories(file.getParent());
            Files.writeString(file,line+System.lineSeparator(),StandardOpenOption.CREATE,StandardOpenOption.APPEND);
        }
    }
    static class ReportGenerator {
        static String generate(Path logsDir,Path reportFile)throws IOException{
            long deposits=0,withdrawals=0; StringBuilder report=new StringBuilder();
            if(Files.exists(logsDir)) try(DirectoryStream<Path> stream=Files.newDirectoryStream(logsDir,"*.log")){
                for(Path p:stream){
                    if(Files.size(p)==0)continue;
                    try(BufferedReader br=Files.newBufferedReader(p)){
                        String line;
                        while((line=br.readLine())!=null){
                            String[] parts=line.trim().split("\\s+");
                            if(parts.length>=3)try{
                                long amount=Long.parseLong(parts[2]);
                                if(parts[0].equals("DEPOSIT"))deposits+=amount;
                                if(parts[0].equals("WITHDRAW"))withdrawals+=amount;
                            }catch(NumberFormatException ignored){}
                        }
                    }
                    BasicFileAttributes a=Files.readAttributes(p,BasicFileAttributes.class);
                    report.append(p.getFileName()).append(" size=").append(a.size()).append(" modified=").append(a.lastModifiedTime()).append('\n');
                }
            }
            report.append("Total deposits: ").append(deposits).append('\n')
                  .append("Total withdrawals: ").append(withdrawals).append('\n')
                  .append("Net change: ").append(deposits-withdrawals).append('\n');
            Files.createDirectories(reportFile.getParent());
            Files.writeString(reportFile,report.toString()); return report.toString();
        }
    }
    static class TransactionProcessor {
        private final ExecutorService pool=Executors.newFixedThreadPool(4);
        void submit(Runnable task){pool.execute(task);}
        void stop() throws InterruptedException {
            pool.shutdown();
            if(!pool.awaitTermination(30,TimeUnit.SECONDS))pool.shutdownNow();
        }
    }
    static class AccountWorker implements Runnable {
        private final Account account; private final int times; private final long amount;
        AccountWorker(Account account,int times,long amount){this.account=account;this.times=times;this.amount=amount;}
        @Override public void run(){
            System.out.println("Worker: "+Thread.currentThread().getName());
            for(int i=0;i<times;i++)try{account.deposit(amount);}catch(InvalidAmountException e){throw new RuntimeException(e);}
        }
    }
    static class BankService {
        private final Repository<Account> repo=new Repository<>();
        private final Path dataFile=Path.of("data","accounts.dat");
        private final Path logFile=Path.of("data","logs","transactions.log");
        BankService(){
            try{for(Account a:StatePersister.load(dataFile))repo.save(a);}
            catch(Exception e){System.out.println("Could not load saved accounts: "+e.getMessage());}
        }
        @Audit Account openAccount(String name,long opening,int type)throws BankException{
            if(name==null||name.isBlank())throw new BankException("Owner name is required");
            if(opening<0)throw new InvalidAmountException();
            Account a=switch(type){
                case 1 -> new SavingsAccount(name,opening,500);
                case 2 -> new CurrentAccount(name,opening,1000);
                default -> new FixedDepositAccount(name,opening);
            };
            repo.save(a); log("OPEN "+a.getAccountNumber()+" "+opening); return a;
        }
        @Audit void deposit(String id,long amount)throws BankException{
            Account a=require(id);a.deposit(amount);log("DEPOSIT "+id+" "+amount);
        }
        @Audit void withdraw(String id,long amount)throws BankException{
            Account a=require(id);a.withdraw(amount);log("WITHDRAW "+id+" "+amount);
        }
        @Audit void transfer(String from,String to,long amount)throws BankException{
            if(amount<=0)throw new InvalidAmountException();
            Account a=require(from),b=require(to);
            Account first=a.compareTo(b)<=0?a:b, second=first==a?b:a;
            synchronized(first){synchronized(second){a.withdraw(amount);b.deposit(amount);}}
            log("WITHDRAW "+from+" "+amount);log("DEPOSIT "+to+" "+amount);
        }
        String statement(String id)throws BankException{return StatementFormatter.buildStatement(require(id));}
        ArrayList<Account> listAccounts(){return repo.findAll();}
        private Account require(String id)throws AccountNotFoundException{
            Account a=repo.findById(id);if(a==null)throw new AccountNotFoundException(id);return a;
        }
        void save()throws IOException{
            StatePersister.save(repo.findAll().toArray(Account[]::new),dataFile);
        }
        String report()throws IOException{return ReportGenerator.generate(logFile.getParent(),Path.of("data","report.txt"));}
        void batchDemo()throws InterruptedException{
            TransactionProcessor tp=new TransactionProcessor();
            for(Account a:repo.findAll())tp.submit(()->{for(int i=0;i<10;i++)try{a.deposit(1);}catch(InvalidAmountException ignored){}});
            tp.stop();
        }
        private void log(String s)throws BankException{
            try{TransactionLog.append(logFile,s);}catch(IOException e){throw new BankException("Transaction succeeded but log failed: "+e.getMessage());}
        }
    }

    private static long readLong(Scanner sc,String prompt){
        while(true){System.out.print(prompt);String s=sc.nextLine().trim();
            try{return Long.parseLong(s);}catch(NumberFormatException e){System.out.println("Enter a whole number.");}}
    }
    public static void main(String[] args) {
        System.out.println(new BankInfo("MiniBank","Main Branch"));
        BankService bank=new BankService();
        try(Scanner sc=new Scanner(System.in)){
            boolean running=true;
            while(running){
                System.out.println("\n1.Open account  2.Deposit  3.Withdraw  4.Transfer");
                System.out.println("5.Statement  6.List accounts  7.Report  8.Exit");
                long choice=readLong(sc,"Choose: ");
                try{
                    switch((int)choice){
                        case 1 -> {
                            System.out.print("Owner name: ");String name=sc.nextLine();
                            long opening=readLong(sc,"Opening balance: ");
                            System.out.println("1.Savings 2.Current 3.Fixed Deposit");
                            int type=(int)readLong(sc,"Account type: ");
                            Account a=bank.openAccount(name,opening,type);
                            System.out.println("Account created: "+a);
                        }
                        case 2 -> {System.out.print("Account number: ");String id=sc.nextLine();bank.deposit(id,readLong(sc,"Amount: "));System.out.println("Deposit successful.");}
                        case 3 -> {System.out.print("Account number: ");String id=sc.nextLine();bank.withdraw(id,readLong(sc,"Amount: "));System.out.println("Withdrawal successful.");}
                        case 4 -> {System.out.print("From account: ");String from=sc.nextLine();System.out.print("To account: ");String to=sc.nextLine();bank.transfer(from,to,readLong(sc,"Amount: "));System.out.println("Transfer successful.");}
                        case 5 -> {System.out.print("Account number: ");System.out.println(bank.statement(sc.nextLine()));}
                        case 6 -> {for(Account a:bank.listAccounts())System.out.println(a);}
                        case 7 -> System.out.println(bank.report());
                        case 8 -> {bank.save();System.out.println("Goodbye. Data saved.");running=false;}
                        default -> System.out.println("Invalid option.");
                    }
                }catch(BankException|IOException e){System.out.println("Operation failed: "+e.getMessage());}
            }
        }catch(Exception e){System.out.println("MiniBank stopped: "+e.getMessage());}
    }
}
