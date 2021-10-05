import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class LoginWindow extends JFrame {

    private final ChatClient client;
    JTextField loginField = new JTextField();
    JPasswordField passwordField = new JPasswordField();

    //Radio buttons
    JRadioButton studentradiobutton;
    JRadioButton adminradiobutton;

    JButton loginButton = new JButton("Login");
    JButton registerButton = new JButton("Register");

    public LoginWindow(){
        super("Login");


        loginButton.setBackground(Color.white);
        registerButton.setBackground(Color.red);


        this.client = new ChatClient("20.51.187.122", 8818);
        client.connect();



        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



        //have to edit


        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));

        //Declaration of radio
        studentradiobutton = new JRadioButton(); // declartion of radio button for student
        adminradiobutton = new JRadioButton();  // declaration for admin
        adminradiobutton.setText("admin");     // text setting for admin radio button
        studentradiobutton.setText("student"); // text setting for student radio button
        adminradiobutton.setBounds(100, 100, 100, 100);       // dimensions and size for radio buttons
        studentradiobutton.setBounds(100, 100, 100, 100);    // sameies


        p.add(loginField);
        p.add(passwordField);

        //add radio button
        p.add(studentradiobutton);
        p.add(adminradiobutton);

        p.add(loginButton);
        p.add(registerButton);


        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doLogin();
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doRegistration();
            }
        });

        getContentPane().add(p, BorderLayout.CENTER);
        pack();
        setVisible(true);

    }

    private void doRegistration() {
        JOptionPane.showMessageDialog(this,"Cannot register. Database not connected. ");
    }

    private void doLogin() {
        String login = loginField.getText();
        String password = passwordField.getText();

        try {
            if(client.login(login, password)){
                //bring up user list window
                setVisible(false);
                UserListPane userListPane = new UserListPane(client);

                JFrame frame = new JFrame("User List");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(400,600);

                frame.getContentPane().add(userListPane, BorderLayout.CENTER);
                frame.setVisible(true);

            } else {
                //show up error
                JOptionPane.showMessageDialog(this,"Invalid login/password");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        LoginWindow loginWindow = new LoginWindow();
        loginWindow.setVisible(true);
    }
}
