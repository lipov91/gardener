package com.survivingwithandroid.actionbartabnavigation.comunicator;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.survivingwithandroid.actionbartabnavigation.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Jan Lipka on 2015-07-31.
 */
public class BluetoothActivity extends Activity {

    private static final int BT_DISCOVERABLE_DURATION = 300;
    private static final String DEBUG_TAG = "BluetoothActivity";
    private static final UUID SIMPLE_BT_APP_UUID = UUID.fromString("0dfb786a-cafe-feed-cafe-982fdfe4bcbf");
    private static final String SIMPLE_BT_NAME = "SimpleBT";
    private static final int DEVICE_PICKER_DIALOG = 1001;
    private final Handler handler = new Handler();
    private BluetoothAdapter btAdapter;
    private BtReceiver btReceiver;
    private ServerListenThread serverListenThread;
    private ClientConnectThread clientConnectThread;
    private BluetoothDataCommThread bluetoothDataCommThread;
    private BluetoothDevice remoteDevice;
    private BluetoothSocket activeBluetoothSocket;
    private ToggleButton btToggle;
    private TextView statusField;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        if (btAdapter == null) {

            setStatus("Opcja Bluetooth jest niedostêpna");
            disableAllButtons();

        } else {

            btReceiver = new BtReceiver();
            IntentFilter stateChangedFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            IntentFilter actionFoundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            int currentState;

            setStatus("Opcja Bluetooth jest dostêpna");
            this.registerReceiver(btReceiver, stateChangedFilter);
            registerReceiver(btReceiver, actionFoundFilter);
            currentState = btAdapter.getState();
            setUIForBTState(currentState);

            if (currentState == BluetoothAdapter.STATE_ON) {

                findDevices();
            }
        }
    }

    private void findDevices() {

        String lastUsedRemoteDevice = getLastUsedRemoteBTDevice();
        Intent discoverMe = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);

        if (lastUsedRemoteDevice != null) {

            Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

            setStatus("Wyszukiwanie znanych urz¹dzeñ: " + lastUsedRemoteDevice);

            for (BluetoothDevice pairedDevice : pairedDevices) {

                if (pairedDevice.getAddress().equals(lastUsedRemoteDevice)) {

                    setStatus("Znaleziono urz¹dzenie: " + pairedDevice.getName() + "@"
                            + lastUsedRemoteDevice);
                    remoteDevice = pairedDevice;
                }
            }
        }

        if (remoteDevice == null) {

            setStatus("Brak urz¹dzeñ");

            if (btAdapter.startDiscovery()) {

                setStatus("Wyszukiwanie urz¹dzeñ...");
            }
        }

        setStatus("Udostêpnianie Twojego urz¹dzenia...");

        discoverMe.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, BT_DISCOVERABLE_DURATION);
        startActivity(discoverMe);

        setStatus("Nas³uchiwanie...");
        serverListenThread = new ServerListenThread();
        serverListenThread.start();
    }

    private String getLastUsedRemoteBTDevice() {

        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        String result = prefs.getString("LAST_REMOTE_DEVICE_ADDRESS", null);

        return result;
    }

    private void setLastUsedRemoteBTDevice(String name) {

        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("LAST_REMOTE_DEVICE_ADDRESS", name);
        edit.commit();
    }

    private void disableAllButtons() {

        Button button;
        int[] buttonIds = {R.id.btnSwitch, R.id.btnConnect, R.id.btnSendMessage};

        for (int buttonId : buttonIds) {

            button = (Button) findViewById(buttonId);
            button.setEnabled(false);
        }
    }

    private void setUIForBTState(int state) {

        if (btToggle == null) {

            btToggle = (ToggleButton) findViewById(R.id.btnSwitch);
        }

        switch (state) {
            case BluetoothAdapter.STATE_ON:
                btToggle.setChecked(true);
                btToggle.setEnabled(true);
                setStatus("STATE_ON");
                break;
            case BluetoothAdapter.STATE_OFF:
                btToggle.setChecked(false);
                btToggle.setEnabled(true);
                setStatus("STATE_OFF");
                break;
            case BluetoothAdapter.STATE_TURNING_OFF:
                btToggle.setChecked(true);
                btToggle.setEnabled(false);
                setStatus("STATE_TURNING_OFF");
                break;
            case BluetoothAdapter.STATE_TURNING_ON:
                btToggle.setChecked(false);
                btToggle.setEnabled(false);
                setStatus("STATE_TURNING_ON");
                break;
        }
    }

    private void setStatus(String string) {

        if (statusField == null) {

            statusField = (TextView) findViewById(R.id.tvStatus);
        }

        String current = (String) statusField.getText();
        current = string + "\n" + current;

        if (current.length() > 1500) {

            int truncPoint = current.lastIndexOf("\n");
            current = (String) current.subSequence(0, truncPoint);
        }

        statusField.setText(current);
    }

    public void onClick(View view) {

        if (view.getId() == R.id.btnSwitch) {

            if (btToggle == null) {

                btToggle = (ToggleButton) findViewById(R.id.btnSwitch);
            }

            if (btToggle.isChecked() == false) {

                if (serverListenThread != null) {

                    serverListenThread.stopListening();
                }

                if (clientConnectThread != null) {

                    clientConnectThread.stopConnecting();
                }

                if (bluetoothDataCommThread != null) {

                    bluetoothDataCommThread.disconnect();
                }

                btAdapter.cancelDiscovery();

                if (!btAdapter.disable()) {

                    setStatus("Wyst¹pi³ b³¹d podczas ukrywania urz¹dzenia");
                }

                remoteDevice = null;
                activeBluetoothSocket = null;
                serverListenThread = null;
                clientConnectThread = null;
                bluetoothDataCommThread = null;

            } else {

                if (!btAdapter.enable()) {

                    setStatus("Wyst¹pi³ b³¹d podczas udostêpniania urz¹dzenia");
                }
            }

        } else if (view.getId() == R.id.btnConnect) {

            if (remoteDevice != null) {

                doConnectToDevice(remoteDevice);
            } else {

                showDialog(DEVICE_PICKER_DIALOG);
            }

        } else if (view.getId() == R.id.btnSendMessage) {

            EditText etMessage = (EditText) findViewById(R.id.etMessage);

            if (bluetoothDataCommThread != null) {

                bluetoothDataCommThread.send(etMessage.getText().toString());
                Toast.makeText(this, "Twoja wiadomoœæ zosta³a wys³ana", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void doConnectToDevice(BluetoothDevice device) {

        btAdapter.cancelDiscovery();
        setStatus("Nawi¹zywanie po³¹czenia...");
        clientConnectThread = new ClientConnectThread(device);
        clientConnectThread.start();
    }

    public void doStartDataCommThread() {

        if (activeBluetoothSocket == null) {

            setStatus("Can't start datacomm");

        } else {

            setStatus("Data comm thread starting");
            bluetoothDataCommThread = new BluetoothDataCommThread(activeBluetoothSocket);
            bluetoothDataCommThread.start();
        }
    }


    public void doHandleReceivedCommand(String rawCommand) {

        String command = rawCommand.trim();
        setStatus("Polecenie: " + command);

        bluetoothDataCommThread.send("message");
    }

    private class BtReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {

                setStatus("ACTION_STATE_CHANGED");
                int currentState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.STATE_OFF);
                setUIForBTState(currentState);

                if (currentState == BluetoothAdapter.STATE_ON) {

                    findDevices();
                }

            } else if (action.equals(BluetoothDevice.ACTION_FOUND)) {

                setStatus("ACTION_FOUND");
                BluetoothDevice foundDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                setStatus("Urz¹dzenie: " + foundDevice.getName() + "@" + foundDevice.getAddress());
            }
        }
    }

    @Override
    protected void onDestroy() {

        if (serverListenThread != null) {

            serverListenThread.stopListening();
        }

        if (clientConnectThread != null) {

            clientConnectThread.stopConnecting();
        }

        if (bluetoothDataCommThread != null) {

            bluetoothDataCommThread.disconnect();
        }

        if (activeBluetoothSocket != null) {

            try {

                activeBluetoothSocket.close();

            } catch (IOException e) {

                Log.e(DEBUG_TAG, "Failed to close socket", e);
            }
        }
        btAdapter.cancelDiscovery();
        this.unregisterReceiver(btReceiver);

        super.onDestroy();
    }


    private class ServerListenThread extends Thread {

        private final BluetoothServerSocket btServerSocket;

        public ServerListenThread() {

            BluetoothServerSocket btServerSocket = null;

            try {

                btServerSocket = btAdapter.listenUsingRfcommWithServiceRecord(SIMPLE_BT_NAME,
                        SIMPLE_BT_APP_UUID);

            } catch (IOException e) {

                Log.e(DEBUG_TAG, "Failed to start listening", e);
            }


            this.btServerSocket = btServerSocket;
        }

        public void run() {

            BluetoothSocket socket = null;

            try {

                while (true) {

                    handler.post(new Runnable() {
                        public void run() {

                            setStatus("ServerThread: calling accept");
                        }
                    });

                    socket = btServerSocket.accept();

                    if (socket != null) {

                        activeBluetoothSocket = socket;


                        handler.post(new Runnable() {

                            public void run() {

                                setStatus("Got a device socket");
                                doStartDataCommThread();
                            }
                        });

                        btServerSocket.close();
                        break;
                    }
                }

            } catch (Exception e) {

                handler.post(new Runnable() {

                    public void run() {
                        setStatus("Listening socket done - failed or cancelled");
                    }
                });
            }
        }

        public void stopListening() {

            try {

                btServerSocket.close();

            } catch (Exception e) {

                Log.e(DEBUG_TAG, "Failed to close listening socket", e);
            }
        }
    }

    // client thread: used to make a synchronous connect call to a device
    private class ClientConnectThread extends Thread {

        private final BluetoothDevice remoteDevice;
        private final BluetoothSocket clientSocket;

        public ClientConnectThread(BluetoothDevice remoteDevice) {

            this.remoteDevice = remoteDevice;
            BluetoothSocket clientSocket = null;

            try {

                clientSocket = remoteDevice.createRfcommSocketToServiceRecord(SIMPLE_BT_APP_UUID);

            } catch (IOException e) {

                Log.e(DEBUG_TAG, "Failed to open local client socket");
            }

            this.clientSocket = clientSocket;
        }

        public void run() {

            boolean success = false;

            try {

                clientSocket.connect();
                success = true;

            } catch (IOException e) {

                Log.e(DEBUG_TAG, "Client connect failed or cancelled");

                try {

                    clientSocket.close();

                } catch (IOException e1) {

                    Log.e(DEBUG_TAG, "Failed to close socket on error", e);
                }
            }

            final String status;

            if (success) {

                status = "Connected to remote device";
                activeBluetoothSocket = clientSocket;

                serverListenThread.stopListening();

            } else {

                status = "Failed to connect to remote device";
                activeBluetoothSocket = null;
            }

            handler.post(new Runnable() {

                public void run() {

                    setStatus(status);
                    setLastUsedRemoteBTDevice(remoteDevice.getAddress());
                    doStartDataCommThread();
                }
            });
        }

        public void stopConnecting() {

            try {

                clientSocket.close();

            } catch (Exception e) {

                Log.e(DEBUG_TAG, "Failed to stop connecting", e);
            }
        }
    }

    private class BluetoothDataCommThread extends Thread {

        private final BluetoothSocket dataSocket;
        private final OutputStream outData;
        private final InputStream inData;

        public BluetoothDataCommThread(BluetoothSocket dataSocket) {

            this.dataSocket = dataSocket;
            OutputStream outData = null;
            InputStream inData = null;

            try {

                outData = dataSocket.getOutputStream();
                inData = dataSocket.getInputStream();

            } catch (IOException e) {

                Log.e(DEBUG_TAG, "Failed to get iostream", e);
            }

            this.inData = inData;
            this.outData = outData;
        }

        public void run() {

            byte[] readBuffer = new byte[64];
            int readSize = 0;

            try {

                while (true) {

                    readSize = inData.read(readBuffer);

                    final String inStr = new String(readBuffer, 0, readSize);
                    handler.post(new Runnable() {

                        public void run() {

                            doHandleReceivedCommand(inStr);
                        }
                    });
                }

            } catch (Exception e) {

                Log.e(DEBUG_TAG, "Socket failure or closed", e);
            }
        }

        public boolean send(String out) {

            boolean success = false;

            try {

                outData.write(out.getBytes(), 0, out.length());
                success = true;

            } catch (IOException e) {

                Log.e(DEBUG_TAG, "Failed to write to remote device", e);
                setStatus("Send failed");
            }

            return success;
        }

        public void disconnect() {

            try {

                dataSocket.close();

            } catch (Exception e) {

                Log.e(DEBUG_TAG, "Failed to close datacomm socket", e);
            }
        }
    }
}
