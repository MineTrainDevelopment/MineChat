package de.minetrain.minechat.utils.audio;

import java.util.ArrayList;

import org.slf4j.LoggerFactory;

import de.minetrain.minechat.config.enums.AutoReplyState;

public enum AudioVolume {
	VOLUME_5(0.05),
	VOLUME_10(0.1),
	VOLUME_15(0.15),
	VOLUME_20(0.2),
	VOLUME_25(0.25),
	VOLUME_30(0.3),
	VOLUME_35(0.35),
	VOLUME_40(0.4),
	VOLUME_45(0.45),
	VOLUME_50(0.5),
	VOLUME_55(0.55),
	VOLUME_60(0.6),
	VOLUME_65(0.65),
	VOLUME_70(0.7),
	VOLUME_75(0.75),
	VOLUME_80(0.8),
	VOLUME_85(0.85),
	VOLUME_90(0.9),
	VOLUME_95(0.95),
	VOLUME_100(1.0);
	
	private Double volume;
	
	private AudioVolume(Double volume) {
		this.volume = volume;
	}
	
	public String getText(){
		return "Volume: "+this.name().replace("VOLUME_", "")+"%";
	}
	
	public Double getValue(){
		return volume;
	}
	
	public static final ArrayList<AudioVolume> getAll(){
		ArrayList<AudioVolume> list = new ArrayList<AudioVolume>();
		list.add(VOLUME_100);
		list.add(VOLUME_95);
		list.add(VOLUME_90);
		list.add(VOLUME_85);
		list.add(VOLUME_80);
		list.add(VOLUME_75);
		list.add(VOLUME_70);
		list.add(VOLUME_65);
		list.add(VOLUME_60);
		list.add(VOLUME_55);
		list.add(VOLUME_50);
		list.add(VOLUME_45);
		list.add(VOLUME_40);
		list.add(VOLUME_35);
		list.add(VOLUME_30);
		list.add(VOLUME_25);
		list.add(VOLUME_20);
		list.add(VOLUME_15);
		list.add(VOLUME_10);
		list.add(VOLUME_5);
		return list;
	}
	
	public static AudioVolume get(String name) {
		if(name == null){
			return VOLUME_100;
		}
		
		try {
			return AudioVolume.valueOf(name);
		} catch (IllegalArgumentException ex) {
			LoggerFactory.getLogger(AutoReplyState.class).warn("Invalid AudioVolume -> "+name);
			return VOLUME_100;
		}
	}
	
	@Override
	public String toString() {
		return getText();
	}
}
