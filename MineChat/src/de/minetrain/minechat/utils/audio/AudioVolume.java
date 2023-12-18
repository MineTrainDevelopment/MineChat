package de.minetrain.minechat.utils.audio;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
	
	public int getIntValue(){
		return Integer.valueOf(this.name().replace("VOLUME_", ""));
	}
	
	public Double getValue(){
		return volume;
	}
	
	public static final List<AudioVolume> getAll(){
		return Arrays.asList(values());
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
	
	public static AudioVolume get(int value) {
		if(value > 100 || value <5){
			return value > 100 ? VOLUME_100 : VOLUME_5;
		}

		try {
			return get(getAll().stream()
					.map(AudioVolume::name)
					.filter(name -> name.contains(String.valueOf(((Math.round((value / 10.0) * 2) / 2.0) * 10.0)).replace(".0", "")))
					.collect(Collectors.toList())
					.get(0));
			
		} catch (IllegalArgumentException | IndexOutOfBoundsException ex) {
			LoggerFactory.getLogger(AutoReplyState.class).warn("Invalid AudioVolume -> "+value);
			return VOLUME_100;
		}
	}

	@Override
	public String toString() {
		return getText();
	}
}
