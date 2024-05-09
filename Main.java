import javax.swing.SwingUtilities;

public static void main(String[] args) {
    // Create an instance of ATM
    ATM atm = new ATM();

    // Start the ATM GUI
    SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
            // Create and start the ATM GUI, passing the ATM instance to it
            ATMGUI atmGUI = new ATMGUI(atm);
            atmGUI.start();
        }
    });

    // Start the ATM (assuming it contains logic to run the ATM functionality)
    atm.start();
}





