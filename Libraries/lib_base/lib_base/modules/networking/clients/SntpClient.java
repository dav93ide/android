package com.bodhitech.it.lib_base.lib_base.modules.networking.clients;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ExecutionException;

public class SntpClient {

    private static final String TAG = SntpClient.class.getSimpleName();
    // Ntp
    public static final String NTP_SERVER = "time.google.com";
    public static final int NTP_TIMEOUT = 2 * 1000; // 2 secondi il timeout
    // Time Offset Data
    private static final int REFERENCE_TIME_OFFSET = 16;
    private static final int ORIGINATE_TIME_OFFSET = 24;
    private static final int RECEIVE_TIME_OFFSET = 32;
    private static final int TRANSMIT_TIME_OFFSET = 40;
    // NTP Data
    private static final int NTP_PACKET_SIZE = 48;
    private static final int NTP_PORT = 123;
    private static final int NTP_MODE_CLIENT = 3;
    private static final int NTP_VERSION = 3;
    // Number of seconds between Jan 1, 1900 and Jan 1, 1970
    // 70 years plus 17 leap days
    private static final long OFFSET_1900_TO_1970 = ((365L * 70L) + 17L) * 24L * 60L * 60L;

    // system time computed from NTP server response
    private long mNtpTime;
    // value of SystemClock.elapsedRealtime() corresponding to mNtpTime
    private long mNtpTimeReference;
    // round trip time in milliseconds
    private long mRoundTripTime;

    /**
     * Returns the time computed from the NTP transaction.
     *
     * @return time value computed from NTP server response.
     */
    public long getNtpTime() {
        return mNtpTime;
    }

    public void setNtpTime(long ntpTime){
        mNtpTime = ntpTime;
    }

    /**
     * Returns the reference clock value (value of SystemClock.elapsedRealtime())
     * corresponding to the NTP time.
     *
     * @return reference clock corresponding to the NTP time.
     */
    public long getNtpTimeReference() {
        return mNtpTimeReference;
    }

    public void setNtpTimeReference(long ntpTimeReference){
        mNtpTimeReference = ntpTimeReference;
    }

    /**
     * Returns the round trip time of the NTP transaction
     *
     * @return round trip time in milliseconds.
     */
    public long getRoundTripTime() {
        return mRoundTripTime;
    }

    public void setRoundTripTime(long roundTripTime){
        mRoundTripTime = roundTripTime;
    }

    /**
     * Sends an SNTP request to the given host and processes the response.
     *
     * @param host    host name of the server.
     * @param timeout network timeout in milliseconds.
     * @return true if the transaction was successful.
     */
    public boolean requestTime(String host, int timeout) {
        try {
            return new RequestTimeTask(this, host, timeout).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();
        } catch (InterruptedException iE) {
            if(!TextUtils.isEmpty(iE.getMessage())){
                Log.e(TAG, String.format("No Message for %1$s", iE.getClass().getSimpleName()));
            } else {
                Log.e(TAG, iE.getMessage());
            }
            iE.printStackTrace();
        } catch (ExecutionException eE) {
            if(!TextUtils.isEmpty(eE.getMessage())){
                Log.e(TAG, String.format("No Message for %1$s", eE.getClass().getSimpleName()));
            } else {
                Log.e(TAG, eE.getMessage());
            }
            eE.printStackTrace();
        }
        return false;
    }

    /**
     * Reads an unsigned 32 bit big endian number from the given offset in the buffer.
     */
    private long read32(byte[] buffer, int offset) {
        byte b0 = buffer[offset];
        byte b1 = buffer[offset + 1];
        byte b2 = buffer[offset + 2];
        byte b3 = buffer[offset + 3];

        // convert signed bytes to unsigned values
        int i0 = ((b0 & 0x80) == 0x80 ? (b0 & 0x7F) + 0x80 : b0);
        int i1 = ((b1 & 0x80) == 0x80 ? (b1 & 0x7F) + 0x80 : b1);
        int i2 = ((b2 & 0x80) == 0x80 ? (b2 & 0x7F) + 0x80 : b2);
        int i3 = ((b3 & 0x80) == 0x80 ? (b3 & 0x7F) + 0x80 : b3);

        return ((long) i0 << 24) + ((long) i1 << 16) + ((long) i2 << 8) + (long) i3;
    }

    /**
     * Reads the NTP time stamp at the given offset in the buffer and returns
     * it as a system time (milliseconds since January 1, 1970).
     */
    private long readTimeStamp(byte[] buffer, int offset) {
        long seconds = read32(buffer, offset);
        long fraction = read32(buffer, offset + 4);
        return ((seconds - OFFSET_1900_TO_1970) * 1000) + ((fraction * 1000L) / 0x100000000L);
    }

    /**
     * Writes system time (milliseconds since January 1, 1970) as an NTP time stamp
     * at the given offset in the buffer.
     */
    private void writeTimeStamp(byte[] buffer, int offset, long time) {
        long seconds = time / 1000L;
        long milliseconds = time - seconds * 1000L;
        seconds += OFFSET_1900_TO_1970;

        // write seconds in big endian format
        buffer[offset++] = (byte) (seconds >> 24);
        buffer[offset++] = (byte) (seconds >> 16);
        buffer[offset++] = (byte) (seconds >> 8);
        buffer[offset++] = (byte) (seconds >> 0);

        long fraction = milliseconds * 0x100000000L / 1000L;
        // write fraction in big endian format
        buffer[offset++] = (byte) (fraction >> 24);
        buffer[offset++] = (byte) (fraction >> 16);
        buffer[offset++] = (byte) (fraction >> 8);
        // low order bits should be random data
        buffer[offset++] = (byte) (Math.random() * 255.0);
    }

    /** Private Classes **/
    private static class RequestTimeTask extends AsyncTask<Void, Void, Boolean> {

        private SntpClient mOuter;
        private String mHost;
        private int mTimeout;

        RequestTimeTask(SntpClient outer, String host, int timeout){
            mOuter = outer;
            mHost = host;
            mTimeout = timeout;
        }

        /** Override AsyncTask Methods **/
        @Override
        protected Boolean doInBackground(Void... voids) {
            try (DatagramSocket socket = new DatagramSocket()) {
                socket.setSoTimeout(mTimeout);
                InetAddress address = InetAddress.getByName(mHost);
                byte[] buffer = new byte[NTP_PACKET_SIZE];
                DatagramPacket request = new DatagramPacket(buffer, buffer.length, address, NTP_PORT);

                // set mode = 3 (client) and version = 3
                // mode is in low 3 bits of first byte
                // version is in bits 3-5 of first byte
                buffer[0] = NTP_MODE_CLIENT | (NTP_VERSION << 3);

                // get current time and write it to the request packet
                long requestTime = System.currentTimeMillis();
                long requestTicks = SystemClock.elapsedRealtime();
                mOuter.writeTimeStamp(buffer, TRANSMIT_TIME_OFFSET, requestTime);

                socket.send(request);

                // read the response
                DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                socket.receive(response);
                long responseTicks = SystemClock.elapsedRealtime();
                long responseTime = requestTime + (responseTicks - requestTicks);

                // extract the results
                long originateTime = mOuter.readTimeStamp(buffer, ORIGINATE_TIME_OFFSET);
                long receiveTime = mOuter.readTimeStamp(buffer, RECEIVE_TIME_OFFSET);
                long transmitTime = mOuter.readTimeStamp(buffer, TRANSMIT_TIME_OFFSET);
                long roundTripTime = responseTicks - requestTicks - (transmitTime - receiveTime);
                // receiveTime = originateTime + transit + skew
                // responseTime = transmitTime + transit - skew
                // clockOffset = ((receiveTime - originateTime) + (transmitTime - responseTime))/2
                //             = ((originateTime + transit + skew - originateTime) +
                //                (transmitTime - (transmitTime + transit - skew)))/2
                //             = ((transit + skew) + (transmitTime - transmitTime - transit + skew))/2
                //             = (transit + skew - transit + skew)/2
                //             = (2 * skew)/2 = skew
                long clockOffset = ((receiveTime - originateTime) + (transmitTime - responseTime)) / 2;
                // if (false) Log.d(TAG, "round trip: " + roundTripTime + " ms");
                // if (false) Log.d(TAG, "clock offset: " + clockOffset + " ms");

                // save our results - use the times on this side of the network latency
                // (response rather than request time)
                mOuter.setNtpTime(responseTime + clockOffset);
                mOuter.setNtpTimeReference(responseTicks);
                mOuter.setRoundTripTime(roundTripTime);
                return true;
            } catch (Exception e) {
                if(TextUtils.isEmpty(e.getMessage())){
                    Log.e(TAG, String.format("No Message for %1$s", e.getClass().getSimpleName()));
                } else {
                    Log.e(TAG, e.getMessage());
                }
                e.printStackTrace();
                return false;
            }
        }

    }

}
