import React, { useState } from 'react';
import { View, Text, StyleSheet, TextInput, TouchableOpacity, ScrollView } from 'react-native';

const AccountScreen = () => {
  const [feedback, setFeedback] = useState('');
  const [isConnected, _setIsConnected] = useState(true); // This would come from NetInfo

  const handleSubmitFeedback = () => {
    // Here we would send the feedback to the server
    console.log('Feedback submitted:', feedback);
    setFeedback('');
  };

  const handleLogout = () => {
    // Here we would handle the logout logic
    console.log('Logout');
  };

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Mon Compte</Text>
        <View style={styles.connectionStatus}>
          <View style={[
            styles.statusDot,
            { backgroundColor: isConnected ? '#34C759' : '#FF3B30' }
          ]} />
          <Text style={styles.statusText}>
            {isConnected ? 'Connecté' : 'Hors ligne'}
          </Text>
        </View>
      </View>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Informations personnelles</Text>
        <View style={styles.infoItem}>
          <Text style={styles.infoLabel}>Nom</Text>
          <Text style={styles.infoValue}>Dupont</Text>
        </View>
        <View style={styles.infoItem}>
          <Text style={styles.infoLabel}>Prénom</Text>
          <Text style={styles.infoValue}>Jean</Text>
        </View>
        <View style={styles.infoItem}>
          <Text style={styles.infoLabel}>Matricule</Text>
          <Text style={styles.infoValue}>12345</Text>
        </View>
      </View>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Suggestions d'amélioration</Text>
        <TextInput
          style={styles.feedbackInput}
          placeholder="Vos suggestions pour améliorer l'application..."
          value={feedback}
          onChangeText={setFeedback}
          multiline
          numberOfLines={4}
        />
        <TouchableOpacity 
          style={styles.submitButton}
          onPress={handleSubmitFeedback}
        >
          <Text style={styles.submitButtonText}>Envoyer</Text>
        </TouchableOpacity>
      </View>

      <TouchableOpacity 
        style={styles.logoutButton}
        onPress={handleLogout}
      >
        <Text style={styles.logoutButtonText}>Se déconnecter</Text>
      </TouchableOpacity>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
  header: {
    padding: 16,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    borderBottomWidth: 1,
    borderBottomColor: '#e0e0e0',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
  },
  connectionStatus: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  statusDot: {
    width: 10,
    height: 10,
    borderRadius: 5,
    marginRight: 8,
  },
  statusText: {
    fontSize: 14,
    color: '#666',
  },
  section: {
    padding: 16,
    borderBottomWidth: 1,
    borderBottomColor: '#e0e0e0',
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 16,
  },
  infoItem: {
    marginBottom: 12,
  },
  infoLabel: {
    fontSize: 14,
    color: '#666',
    marginBottom: 4,
  },
  infoValue: {
    fontSize: 16,
  },
  feedbackInput: {
    borderWidth: 1,
    borderColor: '#e0e0e0',
    borderRadius: 8,
    padding: 12,
    marginBottom: 16,
    minHeight: 100,
    textAlignVertical: 'top',
  },
  submitButton: {
    backgroundColor: '#007AFF',
    padding: 12,
    borderRadius: 8,
    alignItems: 'center',
  },
  submitButtonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: 'bold',
  },
  logoutButton: {
    margin: 16,
    padding: 12,
    borderRadius: 8,
    alignItems: 'center',
    backgroundColor: '#FF3B30',
  },
  logoutButtonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: 'bold',
  },
});

export default AccountScreen; 