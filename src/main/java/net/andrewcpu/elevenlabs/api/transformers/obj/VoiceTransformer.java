package net.andrewcpu.elevenlabs.api.transformers.obj;

import net.andrewcpu.elevenlabs.api.transformers.ResultTransformerAdapter;
import net.andrewcpu.elevenlabs.elements.voice.Voice;
import org.json.simple.JSONObject;

public class VoiceTransformer extends ResultTransformerAdapter<Voice> {
	@Override
	public Voice transform(JSONObject object) {
		return Voice.fromJSON(object);
	}
}
