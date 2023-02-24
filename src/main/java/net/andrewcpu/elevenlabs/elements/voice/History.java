package net.andrewcpu.elevenlabs.elements.voice;

import net.andrewcpu.elevenlabs.ElevenLabsAPI;
import net.andrewcpu.elevenlabs.enums.State;
import net.andrewcpu.elevenlabs.exceptions.ElevenLabsAPINotInitiatedException;
import net.andrewcpu.elevenlabs.exceptions.ElevenLabsValidationException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public record History(List<HistoryItem> history) {
	public static History fromJSON(JSONObject object) {
		List<HistoryItem> historyItems = new ArrayList<>();
		JSONArray historyArray = (JSONArray) object.get("history");
		History history = new History(new ArrayList<>());
		for (Object item : historyArray) {
			JSONObject itemJson = (JSONObject) item;
			String historyItemId = (String) itemJson.get("history_item_id");
			String voiceId = (String) itemJson.get("voice_id");
			String voiceName = (String) itemJson.get("voice_name");
			String text = (String) itemJson.get("text");
			long dateUnix = (Long) itemJson.get("date_unix");
			int characterCountChangeFrom = ((Long) itemJson.get("character_count_change_from")).intValue();
			int characterCountChangeTo = ((Long) itemJson.get("character_count_change_to")).intValue();
			String contentType = (String) itemJson.get("content_type");
			String state = (String) itemJson.get("state");
			HistoryItem historyItem = new HistoryItem(historyItemId, voiceId, voiceName, text, dateUnix, characterCountChangeFrom, characterCountChangeTo, contentType, state, history);
			historyItems.add(historyItem);
		}
		history.history.addAll(historyItems);
		return history;
	}

	public static History get() throws ElevenLabsValidationException, IOException, ElevenLabsAPINotInitiatedException {
		return ElevenLabsAPI.getInstance().getHistory();
	}


	public HistoryItem getHistoryItem(String id) {
		for (HistoryItem item : history) {
			if (item.getHistoryItemId().equals(id)) {
				return item;
			}
		}
		return null;
	}

	public File downloadHistory(String[] ids, File file) throws IOException, ElevenLabsValidationException, ElevenLabsAPINotInitiatedException {
		return ElevenLabsAPI.getInstance().downloadHistory(Arrays.stream(ids).toList(), file);
	}

	public File downloadHistory(List<HistoryItem> historyItems, File file) throws IOException, ElevenLabsValidationException, ElevenLabsAPINotInitiatedException {
		return ElevenLabsAPI.getInstance().downloadHistory(historyItems.stream().map(HistoryItem::getHistoryItemId).collect(Collectors.toList()), file);
	}

	@Override
	public String toString() {
		return "History{" +
				"history=" + history +
				'}';
	}

	public static class HistoryItem {
		private final String historyItemId;
		private final String voiceId;
		private final String voiceName;
		private final String text;
		private final long dateUnix;
		private final int characterCountChangeFrom;
		private final int characterCountChangeTo;
		private final String contentType;
		private final State state;
		private Voice voice;
		private final History history;

		public HistoryItem(String historyItemId, String voiceId, String voiceName, String text, long dateUnix, int characterCountChangeFrom, int characterCountChangeTo, String contentType, String state, History history) {
			this.historyItemId = historyItemId;
			this.voiceId = voiceId;
			this.voiceName = voiceName;
			this.text = text;
			this.dateUnix = dateUnix;
			this.characterCountChangeFrom = characterCountChangeFrom;
			this.characterCountChangeTo = characterCountChangeTo;
			this.contentType = contentType;
			this.state = State.valueOf(state.toUpperCase());
			this.history = history;
			this.voice = null;
		}

		public Voice getVoice() {
			if (voice == null) {
				try {
					voice = ElevenLabsAPI.getInstance().getVoice(voiceId);
				} catch (ElevenLabsValidationException | IOException | ElevenLabsAPINotInitiatedException e) {
					throw new RuntimeException(e);
				}
			}
			return voice;
		}

		public String getHistoryItemId() {
			return historyItemId;
		}

		public String getVoiceId() {
			return voiceId;
		}

		public String getVoiceName() {
			return voiceName;
		}

		public String getText() {
			return text;
		}

		public long getDateUnix() {
			return dateUnix;
		}

		public Date getDate() {
			return new Date(dateUnix);
		}

		public int getCharacterCountChangeFrom() {
			return characterCountChangeFrom;
		}

		public int getCharacterCountChangeTo() {
			return characterCountChangeTo;
		}

		public String getContentType() {
			return contentType;
		}

		public State getState() {
			return state;
		}

		public String delete() throws ElevenLabsValidationException, IOException, ElevenLabsAPINotInitiatedException {
			String output = ElevenLabsAPI.getInstance().deleteHistoryItem(this);
			history.history.remove(this);
			return output;
		}

		public File downloadAudio(File outputFile) throws ElevenLabsValidationException, IOException, ElevenLabsAPINotInitiatedException {
			return ElevenLabsAPI.getInstance().getHistoryItemAudio(this, outputFile);
		}

		@Override
		public String toString() {
			return "HistoryItem{" +
					"historyItemId='" + historyItemId + '\'' +
					", voiceId='" + voiceId + '\'' +
					", voiceName='" + voiceName + '\'' +
					", text='" + text + '\'' +
					", dateUnix=" + dateUnix +
					", characterCountChangeFrom=" + characterCountChangeFrom +
					", characterCountChangeTo=" + characterCountChangeTo +
					", contentType='" + contentType + '\'' +
					", state='" + state + '\'' +
					", voice=" + voice +
					'}';
		}
	}
}
