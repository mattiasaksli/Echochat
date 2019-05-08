import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;


class TextSpeech {


    private String text;
    boolean TTSvalue;
    private TextSpeech(String text) {
        this.text = text;
    }

    public static void editMessage(String message) {
        String[] split = message.split(":");
        sayMessage(split[1]);
    }

    private void speak() {
        Voice voice;
        VoiceManager voiceManager = VoiceManager.getInstance();
        voice = voiceManager.getVoice("kevin16");
        voice.allocate();
        voice.speak(text);
    }

    static void sayMessage(String message) {
        System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
        TextSpeech ts = new TextSpeech(message);
        ts.speak();
    }
}