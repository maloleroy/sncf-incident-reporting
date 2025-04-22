import { NativeModules, NativeEventEmitter } from 'react-native';

class SpeechService {
  private static instance: SpeechService;
  private isInitialized: boolean = false;
  private isRecording: boolean = false;
  private eventEmitter: NativeEventEmitter;

  private constructor() {
    this.eventEmitter = new NativeEventEmitter(NativeModules.VoskModule);
  }

  public static getInstance(): SpeechService {
    if (!SpeechService.instance) {
      SpeechService.instance = new SpeechService();
    }
    return SpeechService.instance;
  }

  async initialize(): Promise<void> {
    if (this.isInitialized) return;

    try {
      // Initialize Vosk with the tiny-fr model
      await NativeModules.VoskModule.initialize('tiny-fr');
      this.isInitialized = true;
    } catch (error) {
      console.error('Error initializing Vosk:', error);
      throw error;
    }
  }

  async startRecording(onResult: (text: string) => void): Promise<void> {
    if (!this.isInitialized) {
      await this.initialize();
    }

    if (this.isRecording) {
      console.warn('Recording is already in progress');
      return;
    }

    try {
      this.isRecording = true;
      
      // Start recording
      await NativeModules.VoskModule.startRecording();

      // Listen for recognition results
      const subscription = this.eventEmitter.addListener(
        'onResult',
        (event: { text: string }) => {
          onResult(event.text);
        }
      );

      // Clean up subscription when recording stops
      this.eventEmitter.addListener('onRecordingStopped', () => {
        subscription.remove();
      });
    } catch (error) {
      console.error('Error starting recording:', error);
      this.isRecording = false;
      throw error;
    }
  }

  async stopRecording(): Promise<void> {
    if (!this.isRecording) {
      console.warn('No recording in progress');
      return;
    }

    try {
      await NativeModules.VoskModule.stopRecording();
      this.isRecording = false;
    } catch (error) {
      console.error('Error stopping recording:', error);
      throw error;
    }
  }

  async cleanup(): Promise<void> {
    if (this.isRecording) {
      await this.stopRecording();
    }

    try {
      await NativeModules.VoskModule.cleanup();
      this.isInitialized = false;
    } catch (error) {
      console.error('Error cleaning up Vosk:', error);
      throw error;
    }
  }
}

export default SpeechService.getInstance(); 