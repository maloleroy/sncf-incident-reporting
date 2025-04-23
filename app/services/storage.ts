import AsyncStorage from '@react-native-async-storage/async-storage';
import NetInfo from '@react-native-community/netinfo';
import { Incident, Trip, Feedback } from '../types';

const STORAGE_KEYS = {
  INCIDENTS: '@incidents',
  TRIPS: '@trips',
  FEEDBACK: '@feedback',
  PENDING_SYNC: '@pending_sync',
};

class StorageService {
  private static instance: StorageService;
  private isOnline: boolean = false;

  private constructor() {
    // Subscribe to network state changes
    NetInfo.addEventListener(state => {
      this.isOnline = state.isConnected ?? false;
      if (this.isOnline) {
        this.syncPendingData();
      }
    });
  }

  public static getInstance(): StorageService {
    if (!StorageService.instance) {
      StorageService.instance = new StorageService();
    }
    return StorageService.instance;
  }

  // Incident methods
  async saveIncident(incident: Incident): Promise<void> {
    try {
      const incidents = await this.getIncidents();
      incidents.push(incident);
      await AsyncStorage.setItem(STORAGE_KEYS.INCIDENTS, JSON.stringify(incidents));
      
      if (!this.isOnline) {
        await this.addToPendingSync('incident', incident);
      }
    } catch (error) {
      console.error('Error saving incident:', error);
      throw error;
    }
  }

  async getIncidents(): Promise<Incident[]> {
    try {
      const incidentsJson = await AsyncStorage.getItem(STORAGE_KEYS.INCIDENTS);
      return incidentsJson ? JSON.parse(incidentsJson) : [];
    } catch (error) {
      console.error('Error getting incidents:', error);
      return [];
    }
  }

  // Trip methods
  async saveTrip(trip: Trip): Promise<void> {
    try {
      const trips = await this.getTrips();
      trips.push(trip);
      await AsyncStorage.setItem(STORAGE_KEYS.TRIPS, JSON.stringify(trips));
      
      if (!this.isOnline) {
        await this.addToPendingSync('trip', trip);
      }
    } catch (error) {
      console.error('Error saving trip:', error);
      throw error;
    }
  }

  async getTrips(): Promise<Trip[]> {
    try {
      const tripsJson = await AsyncStorage.getItem(STORAGE_KEYS.TRIPS);
      return tripsJson ? JSON.parse(tripsJson) : [];
    } catch (error) {
      console.error('Error getting trips:', error);
      return [];
    }
  }

  // Feedback methods
  async saveFeedback(feedback: Feedback): Promise<void> {
    try {
      const feedbacks = await this.getFeedback();
      feedbacks.push(feedback);
      await AsyncStorage.setItem(STORAGE_KEYS.FEEDBACK, JSON.stringify(feedbacks));
      
      if (!this.isOnline) {
        await this.addToPendingSync('feedback', feedback);
      }
    } catch (error) {
      console.error('Error saving feedback:', error);
      throw error;
    }
  }

  async getFeedback(): Promise<Feedback[]> {
    try {
      const feedbackJson = await AsyncStorage.getItem(STORAGE_KEYS.FEEDBACK);
      return feedbackJson ? JSON.parse(feedbackJson) : [];
    } catch (error) {
      console.error('Error getting feedback:', error);
      return [];
    }
  }

  // Sync methods
  private async addToPendingSync(type: 'incident' | 'trip' | 'feedback', data: Incident | Trip | Feedback): Promise<void> {
    try {
      const pendingSync = await this.getPendingSync();
      pendingSync.push({ type, data, timestamp: new Date().toISOString() });
      await AsyncStorage.setItem(STORAGE_KEYS.PENDING_SYNC, JSON.stringify(pendingSync));
    } catch (error) {
      console.error('Error adding to pending sync:', error);
    }
  }

  private async getPendingSync(): Promise<Array<{ type: string; data: Incident | Trip | Feedback; timestamp: string }>> {
    try {
      const pendingSyncJson = await AsyncStorage.getItem(STORAGE_KEYS.PENDING_SYNC);
      return pendingSyncJson ? JSON.parse(pendingSyncJson) : [];
    } catch (error) {
      console.error('Error getting pending sync:', error);
      return [];
    }
  }

  private async syncPendingData(): Promise<void> {
    try {
      const pendingSync = await this.getPendingSync();
      if (pendingSync.length === 0) return;

      // Here we would implement the actual sync logic with the server
      console.log('Syncing pending data:', pendingSync);
      
      // After successful sync, clear pending data
      await AsyncStorage.removeItem(STORAGE_KEYS.PENDING_SYNC);
    } catch (error) {
      console.error('Error syncing pending data:', error);
    }
  }
}

export default StorageService.getInstance(); 