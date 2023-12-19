package de.minetrain.minechat.gui.obj.messages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.text.Document;

import de.minetrain.minechat.main.Main;

public class MessageHyperLinks{
	private static final HashMap<Document, List<HyperLink>> hyperLinkCache = new HashMap<Document, List<HyperLink>>();
	
	public record HyperLink(int point, String url){
		public String domain(){
			return Main.extractDomain(url);
		}
	};
	
	public static HyperLink addHyperLink(Document document, int point, String url){
		HyperLink hyperLink = new HyperLink(point, url);
		hyperLinkCache.computeIfAbsent(document, key -> new ArrayList<HyperLink>()).add(hyperLink);
		System.err.println(hyperLinkCache);
		return hyperLink;
	}
	
	public static String getHyperLink(Document document, int point){
		System.err.println(hyperLinkCache);
		Optional<String> url = hyperLinkCache.getOrDefault(document, Collections.emptyList()).stream()
		        .filter(hyperLink -> hyperLink.point == point)
		        .map(HyperLink::url)
		        .findFirst();

		return url.orElse("");
	}
	
	public static void increaseHyperLinkPoint(Document document, HyperLink link, int amound){
		System.err.println(hyperLinkCache);
		hyperLinkCache.get(document).remove(link);
		hyperLinkCache.get(document).add(new HyperLink(link.point+amound, link.url));
		System.err.println(hyperLinkCache);
	};
	
	public static String getToolTipText(Document document){
		String url = hyperLinkCache.getOrDefault(document, Collections.emptyList()).stream()
		        .map(HyperLink::url)
		        .collect(Collectors.joining("  -  "));
		
		return url;
	}
	
}
