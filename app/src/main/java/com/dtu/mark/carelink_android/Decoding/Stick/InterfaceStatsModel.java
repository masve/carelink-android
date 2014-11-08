package com.dtu.mark.carelink_android.Decoding.Stick;

/**
 * Created by mark on 08/11/14.
 */
public class InterfaceStatsModel {
    private InterfaceStatsErrors errors;
    private InterfaceStatsPackets packets;

    private class InterfaceStatsErrors {
        int crc;
        int sequence;
        int naks;
        int timeouts;

        private InterfaceStatsErrors(int crc, int sequence, int naks, int timeouts) {
            this.crc = crc;
            this.sequence = sequence;
            this.naks = naks;
            this.timeouts = timeouts;
        }

        public int getCrc() {
            return crc;
        }

        public int getSequence() {
            return sequence;
        }

        public int getNaks() {
            return naks;
        }

        public int getTimeouts() {
            return timeouts;
        }

        @Override
        public String toString() {
            return "InterfaceStatsErrors{" +
                    "crc=" + crc +
                    ", sequence=" + sequence +
                    ", naks=" + naks +
                    ", timeouts=" + timeouts +
                    '}';
        }
    }
    private class InterfaceStatsPackets {
        int received;
        int transmit;

        private InterfaceStatsPackets(int received, int transmit) {
            this.received = received;
            this.transmit = transmit;
        }

        public int getReceived() {
            return received;
        }

        public int getTransmit() {
            return transmit;
        }

        @Override
        public String toString() {
            return "InterfaceStatsPackets{" +
                    "received=" + received +
                    ", transmit=" + transmit +
                    '}';
        }
    }

    public InterfaceStatsModel(byte crc, byte sequence, byte naks, byte timeouts, int received, int transmit) {
        this.errors = new InterfaceStatsErrors(crc, sequence, naks, timeouts);
        this.packets = new InterfaceStatsPackets(received, transmit);
    }

    public InterfaceStatsErrors getErrors() {
        return errors;
    }

    public InterfaceStatsPackets getPackets() {
        return packets;
    }

    @Override
    public String toString() {
        return "InterfaceStatsModel{" +
                "errors=" + errors.toString() +
                ", packets=" + packets.toString() +
                '}';
    }
}
