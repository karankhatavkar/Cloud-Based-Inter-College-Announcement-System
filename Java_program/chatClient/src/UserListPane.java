import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

/**
 * Created by Karan 18/09/2021
 */


public class UserListPane extends JPanel implements UserStatusListener{


    private final ChatClient client;
    private JList<String> userListUI;
    private DefaultListModel<String> userListModel;
    private JTextField groupField = new JTextField();
    JButton joinButton = new JButton("JOIN");


    public UserListPane(ChatClient client) {
        this.client = client;
        this.client.addUserStatusListener(this);

        userListModel = new DefaultListModel<>();
        userListUI = new JList<>(userListModel);

        joinButton.setBackground(Color.red);
        groupField.setBackground(Color.lightGray);

        setLayout(new BorderLayout());
        //add(new JScrollPane(userListUI), BorderLayout.CENTER);      //old
        //add(inputField,BorderLayout.SOUTH);       //old

        //new placement with joining button
        add(new JScrollPane(userListUI), BorderLayout.PAGE_START);
        add(groupField,BorderLayout.CENTER);
        add(joinButton,BorderLayout.PAGE_END);

        joinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                joinGroup();
            }
        });


        userListUI.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount()>1){
                    String login = userListUI.getSelectedValue();
                    MessagePane messagePane = new MessagePane(client, login);

                    JFrame f = new JFrame("Message : " + login);
                    f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    f.setSize(500, 500);
                    f.getContentPane().add(messagePane, BorderLayout.CENTER);
                    f.setVisible(true);
                }
            }
        });

    }

    private void joinGroup() {
        String topic = "#"+groupField.getText();

    }

    public static void main(String[] args) {
        //ChatClient client = new ChatClient("localhost", 8818);
        ChatClient client = new ChatClient("20.51.187.122", 8818);


        UserListPane userListPane = new UserListPane(client);

        JFrame frame = new JFrame("User List");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400,600);

        frame.getContentPane().add(userListPane, BorderLayout.CENTER);
        frame.setVisible(true);
        if (client.connect()){
            try {
                client.login("guest", "guest");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void online(String userId) {
        userListModel.addElement(userId);
    }

    @Override
    public void offline(String userId) {
        userListModel.removeElement(userId);
    }
}
