import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.VoiceDirectory;
import com.sun.speech.freetts.en.us.cmu_time_awb.AlanVoiceDirectory;
import com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory;


public class TextSpeech {

    private String text;

    public TextSpeech(String text) {
        this.text = text;
    }

    public void speak() {
        Voice voice;
        VoiceManager voiceManager = VoiceManager.getInstance();
        voice = voiceManager.getVoice("kevin16");
        voice.allocate();
        voice.speak(text);
    }

    public static void sayMessage(String message) {
        System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
        TextSpeech ts = new TextSpeech(message);
        ts.speak();
    }
}