interface Switchable {
    void on();
    void off();
    default void toggle() {
        off();
        on();
    }
}
class Fan implements Switchable {
    private boolean isOn = false;
    @Override
    public void on() {
        isOn = true;
        System.out.println("Fan is ON");
    }
    @Override
    public void off() {
        isOn = false;
        System.out.println("Fan is OFF");
    }
}
class Light implements Switchable {
    private boolean isOn = false;
    @Override
    public void on() {
        isOn = true;
        System.out.println("Light is ON");
    }
    @Override
    public void off() {
        isOn = false;
        System.out.println("Light is OFF");
    }
}
@FunctionalInterface
interface SwitchPermission {
    boolean maySwitchOn(Switchable device, int hour);
}
 class RemoteControl {
    public static void main(String[] args) {
        Switchable[] devices = {
            new Fan(),
            new Light()
        };
        System.out.println("=== Toggling Devices ===");
        for (Switchable device : devices) {
            device.toggle();
        }
        SwitchPermission anonymousPermission = new SwitchPermission() {
            @Override
            public boolean maySwitchOn(Switchable device, int hour) {
                return hour >= 6 && hour <= 22;
            }
        };
        System.out.println("\n=== Anonymous Class ===");
        int hour = 20;
        for (Switchable device : devices) {
            if (anonymousPermission.maySwitchOn(device, hour)) {
                device.on();
            } else {
                System.out.println("Device cannot be switched ON at " + hour + ":00");
            }
        }
        SwitchPermission lambdaPermission =
            (device, h) -> h >= 8 && h <= 18;
        System.out.println("\n=== Lambda Expression ===");
        hour = 23;
        for (Switchable device : devices) {
            if (lambdaPermission.maySwitchOn(device, hour)) {
                device.on();
            } else {
                System.out.println("Device cannot be switched ON at " + hour + ":00");
            }
        }
    }
}
