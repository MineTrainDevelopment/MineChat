package de.minetrain.minechat.gui.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.twitch.TwitchManager;
import de.minetrain.minechat.utils.CredentialsManager;

public class GetCredentialsFrame extends JDialog {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(GetCredentialsFrame.class);
	private static final String OAuth2_URL = "https://id.twitch.tv/oauth2/authorize?client_id={CLIENT_ID}&redirect_uri={REDIRECT_URL}&response_type=token&scope=chat:edit+chat:read+channel:moderate+moderation:read";
	private static final String redirectUrl = "http://localhost:8000/oauth_callback";

	private static HttpServer server;
	private JTextField clientIdField, clientSecretField;
	private JLabel statusText;
	private int mouseX, mouseY;
	private JDialog thisDialog = this;
	
	private static String clientId;
	protected static String clientSecret;
	protected static String oAuth2Token;
	//{"status":400,"message":"invalid client"}
	//{"status":403,"message":"invalid client secret"}
	
	public GetCredentialsFrame(JFrame frame) {
    	super(frame, "CredentialsFrame", true);
		setSize(400, 120);
//		setAlwaysOnTop(true);
        setUndecorated(true);
        setLocationRelativeTo(null);
        setBackground(ColorManager.GUI_BUTTON_BACKGROUND);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
        
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createLineBorder(ColorManager.GUI_BORDER, 2));
        panel.setBackground(ColorManager.GUI_BACKGROUND);
//        panel.setLayout(new GridLayout(7, 2));
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        addMouseListener(MoiseListner());
        addMouseMotionListener(mouseMotionListner());


        JPanel statusLabel = new JPanel(new BorderLayout());
        statusLabel.setBackground(getBackground());
        
        statusText = new JLabel("Please fill out the data fields.");
        statusText.setForeground(Color.WHITE);
        statusText.setFont(new Font(null, Font.BOLD, 20));
        statusText.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.add(statusText, BorderLayout.CENTER);
        
        panel.add(statusLabel);
        panel.add(createInputPanel("ClientID:", "id"));
        panel.add(createInputPanel("ClientSecret:", "secret"));
        
        // Erstellen der Buttons
        JButton confirmButton = new JButton("Confirm");
        confirmButton.setBackground(ColorManager.GUI_BACKGROUND);
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setBorder(null);
        confirmButton.addActionListener(closeWindow(false));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(ColorManager.GUI_BACKGROUND);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setBorder(null);
        cancelButton.addActionListener(closeWindow(true));

        // Hinzuf√ºgen der Buttons am unteren Rand des JFrame
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 9, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(7, 2, 2, 2));
        buttonPanel.setBackground(ColorManager.GUI_BORDER);
        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);
        panel.add(buttonPanel);
        
        // Setzen der Breite der Buttons auf die Breite der Textfelder
//        int buttonWidth = (loginNameField.getPreferredSize().width + displayNameField.getPreferredSize().width) / 2;
//        cancelButton.setPreferredSize(new Dimension(buttonWidth, cancelButton.getPreferredSize().height));
//        confirmButton.setPreferredSize(new Dimension(buttonWidth, confirmButton.getPreferredSize().height));

        // Erstellen des Haupt-Panels und Hinzuf√ºgen der Elemente
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createLineBorder(ColorManager.GUI_BORDER, 7));
        mainPanel.setBackground(ColorManager.GUI_BACKGROUND);
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Setzen der Position des Frames relativ zum Haupt-Frame
//        Point location = mainFrame.getLocation();
//        location.setLocation(location.x+50, location.y+200);
//        setLocation(location);

        // Hinzuf√ºgen des Haupt-Panels zum Frame und Anzeigen des Frames
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
	}
	
	private JPanel createInputPanel(String title, String type){
        JPanel panel = new JPanel(new GridLayout(1, 2));
        JPanel textPanel = new JPanel(new BorderLayout());
        JLabel textField = new JLabel(" "+title);
        JTextField inputField = new JTextField();
        JButton button = new JButton("twitch.tv");
        
        inputField.setFont(new Font(null, Font.BOLD, 15));
        inputField.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
        inputField.setForeground(Color.WHITE);
        
        button.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
        button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{Desktop.getDesktop().browse(new URI("https://dev.twitch.tv/console"));}catch(Exception ex){ }
			}
		});

        textField.setForeground(ColorManager.FONT);
        textPanel.setBackground(ColorManager.GUI_BACKGROUND);
        panel.setBackground(ColorManager.GUI_BACKGROUND);
        
        textPanel.add(button, BorderLayout.WEST);
        textPanel.add(textField, BorderLayout.CENTER);
        panel.add(textPanel);
        panel.add(inputField);
        
        if(type.equals("id")){
        	clientIdField = inputField;
        	return panel;
        }
        
        clientSecretField = inputField;
		return panel;
    }
	
	private MouseAdapter MoiseListner() {
		return new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        };
	}

	private MouseAdapter mouseMotionListner() {
		return new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                int newX = e.getXOnScreen() - mouseX;
                int newY = e.getYOnScreen() - mouseY;

                setLocation(newX, newY);
            }
        };
	}
	
	private ActionListener closeWindow(Boolean isCanselt){
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(isCanselt){dispose(); System.exit(1); return;}
				clientId = clientIdField.getText();
				clientSecret = clientSecretField.getText();
				
				try {
					if(clientId.isEmpty() || clientSecret.isEmpty()){
						throw new IllegalArgumentException("Please fill out the data fields.");
					}

					statusText.setText("Validating your data input.");
					TwitchManager.requestAccesToken(clientId, clientSecret);
				} catch (IllegalArgumentException ex) {
					statusText.setText(ex.getMessage());
					return;
				}
				
				try {
					server = HttpServer.create(new InetSocketAddress(8000), 0);
					server.createContext("/oauth_callback", new OAuthCallbackHandler(thisDialog));
					server.start();
					statusText.setText("Please register your twitch client.");
					Desktop.getDesktop().browse(new URI(OAuth2_URL.replace("{CLIENT_ID}", clientId).replace("{REDIRECT_URL}", redirectUrl)));
//					System.out.println("OAuth2 Server gestartet. ÷ffnen Sie den Browser und besuchen Sie http://localhost:8000/oauth_callback");
				} catch (IOException | URISyntaxException ex) {
					logger.error("Error while trying to start the Http server to get the OAuth2 token from the useres Twutch acc.", ex);
				}
//				dispose();
			}
		};
	}
	
	
	static class OAuthCallbackHandler implements HttpHandler {
		private final JDialog dialog;
		public OAuthCallbackHandler(JDialog dialog) {
			this.dialog = dialog;
		}
		
	    @Override
	    public void handle(HttpExchange exchange) throws IOException {
	        // Extrahieren Sie den Autorisierungscode aus der Anfrage
	        String requestURI = exchange.getRequestURI().toString();
	        System.out.println("Query: "+exchange.getRequestURI().getQuery());
	        
	        // Ausgabe der aufgerufenen URL in der Konsole
	        System.out.println("Aufgerufene URL: " + requestURI);

	        // Extrahieren Sie das URL-Fragment mit JavaScript und senden Sie es an den Server
	        String script = "<script>" +
	                "var fragment = window.location.hash.substring(1);" +
	                "var xhr = new XMLHttpRequest();" +
	                "xhr.open('GET', '/oauth_callback/fragment?fragment=' + fragment, true);" +
	                "xhr.send();" +
	                "</script>";

	        // Senden Sie die HTML-Seite mit dem JavaScript-Code zur¸ck zum Client
	        String response = "<html><body>" + script + "</body></html>";
	        exchange.sendResponseHeaders(200, response.length());
	        OutputStream outputStream = exchange.getResponseBody();
	        outputStream.write(response.getBytes());
	        outputStream.close();
	        
	        if(requestURI.contains("fragment=access_token=")){
	        	oAuth2Token = requestURI.substring(0, requestURI.indexOf("&")).replace("/oauth_callback/fragment?fragment=access_token=", "");
	        	server.stop(0);
	        	
	        	try {
					new CredentialsManager(clientId, clientSecret, oAuth2Token);
				} catch (Exception ex) {
					logger.error("Error while trying to save credentials", ex);
				}
	        	
	        	dialog.dispose();
	        }
	    }
	}
}