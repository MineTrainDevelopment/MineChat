package de.minetrain.minechat.gui.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import de.minetrain.minechat.gui.frames.parant.MineDialog;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.twitch.TwitchManager;
import de.minetrain.minechat.twitch.obj.CredentialsManager;

public class GetCredentialsFrame extends MineDialog {
	private static final long serialVersionUID = 8000895546484388251L;
	private static final Logger logger = LoggerFactory.getLogger(GetCredentialsFrame.class);
	private static final String OAuth2_URL = "https://id.twitch.tv/oauth2/authorize?client_id={CLIENT_ID}&redirect_uri={REDIRECT_URL}&response_type=token&scope=chat:edit+chat:read+channel:moderate+moderation:read";
	private static final String redirectUrl = "http://localhost:8000/oauth_callback";

	private static HttpServer server;
	private JTextField clientIdField, clientSecretField;
	private JDialog thisDialog = this;
	
	private static String clientId;
	protected static String clientSecret;
	protected static String oAuth2Token;
	//{"status":400,"message":"invalid client"}
	//{"status":403,"message":"invalid client secret"}
	
	public GetCredentialsFrame(JFrame frame) {
		super(frame, "Please fill out the data fields.", new Dimension(400, 60));
		
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createLineBorder(ColorManager.GUI_BORDER, 2));
        panel.setBackground(ColorManager.GUI_BACKGROUND);
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        panel.add(createInputPanel("ClientID:", "id"));
        panel.add(createInputPanel("ClientSecret:", "secret"));
        
        addContent(panel, BorderLayout.CENTER);
        setConfirmButtonAction(closeWindow());
        setExitOnCancelButton(true);
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
	
	
	private ActionListener closeWindow(){
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				clientId = clientIdField.getText();
				clientSecret = clientSecretField.getText();
				
				try {
					if(clientId.isEmpty() || clientSecret.isEmpty()){
						throw new IllegalArgumentException("Please fill out the data fields.");
					}

					setTitle("Validating your data input.");
					TwitchManager.requestAccesToken(clientId, clientSecret);
				} catch (IllegalArgumentException ex) {
					setTitle(ex.getMessage());
					return;
				}
				
				try {
					server = HttpServer.create(new InetSocketAddress(8000), 0);
					server.createContext("/oauth_callback", new OAuthCallbackHandler(thisDialog));
					server.start();
					setTitle("Please register your twitch client.");
					Desktop.getDesktop().browse(new URI(OAuth2_URL.replace("{CLIENT_ID}", clientId).replace("{REDIRECT_URL}", redirectUrl)));
//					System.out.println("OAuth2 Server gestartet. Öffnen Sie den Browser und besuchen Sie http://localhost:8000/oauth_callback");
				} catch (IOException | URISyntaxException ex) {
					logger.error("Error while trying to start the Http server to get the OAuth2 token from the useres Twutch acc.", ex);
				}
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

	        // Senden Sie die HTML-Seite mit dem JavaScript-Code zurück zum Client
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