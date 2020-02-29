package nisu;

import org.apache.commons.codec.binary.Hex;

import javax.usb.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

public class UsbController {

    public UsbDevice findDevice(UsbHub hub, short vendorId, short productId) {
        for (UsbDevice device : (List<UsbDevice>) hub.getAttachedUsbDevices()) {
            UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();
            System.out.println("Vendor ID " + createByte(desc.idVendor()) + " Product ID " + createByte(desc.idProduct()));
            if (desc.idVendor() == vendorId && desc.idProduct() == productId) return device;
            if (device.isUsbHub()) {
                device = findDevice((UsbHub) device, vendorId, productId);
                if (device != null) return device;
            }
        }
        return null;
    }

    private String createByte(short value) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putShort(value);
        return Hex.encodeHexString(buffer.array());
    }
}
