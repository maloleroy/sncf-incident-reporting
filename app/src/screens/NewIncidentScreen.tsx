import React, { useState } from 'react';
import { View, Text, StyleSheet, TextInput, TouchableOpacity, ScrollView, Alert } from 'react-native';
import { useNavigation } from '@react-navigation/native';

const NewIncidentScreen = () => {
  const navigation = useNavigation();
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [location, setLocation] = useState('');
  const [isRecording, setIsRecording] = useState(false);

  const handleSubmit = () => {
    if (!title || !description || !location) {
      Alert.alert('Erreur', 'Veuillez remplir tous les champs obligatoires');
      return;
    }

    // Here we would save the incident to local storage and sync with server when online
    console.log('Incident submitted:', { title, description, location });
    navigation.goBack();
  };

  const handleRecord = () => {
    setIsRecording(!isRecording);
    // Here we would integrate with Vosk for speech-to-text
    console.log('Recording:', !isRecording);
  };

  return (
    <ScrollView style={styles.container}>
      <View style={styles.form}>
        <View style={styles.inputGroup}>
          <Text style={styles.label}>Titre de l'incident *</Text>
          <TextInput
            style={styles.input}
            placeholder="Entrez le titre de l'incident"
            value={title}
            onChangeText={setTitle}
          />
        </View>

        <View style={styles.inputGroup}>
          <Text style={styles.label}>Description *</Text>
          <TextInput
            style={[styles.input, styles.textArea]}
            placeholder="Décrivez l'incident en détail"
            value={description}
            onChangeText={setDescription}
            multiline
            numberOfLines={4}
          />
          <TouchableOpacity 
            style={styles.recordButton}
            onPress={handleRecord}
          >
            <Text style={styles.recordButtonText}>
              {isRecording ? 'Arrêter l\'enregistrement' : 'Enregistrer la description'}
            </Text>
          </TouchableOpacity>
        </View>

        <View style={styles.inputGroup}>
          <Text style={styles.label}>Localisation *</Text>
          <TextInput
            style={styles.input}
            placeholder="Où l'incident s'est-il produit ?"
            value={location}
            onChangeText={setLocation}
          />
        </View>

        <TouchableOpacity 
          style={styles.submitButton}
          onPress={handleSubmit}
        >
          <Text style={styles.submitButtonText}>Soumettre l'incident</Text>
        </TouchableOpacity>
      </View>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
  form: {
    padding: 16,
  },
  inputGroup: {
    marginBottom: 20,
  },
  label: {
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 8,
  },
  input: {
    borderWidth: 1,
    borderColor: '#e0e0e0',
    borderRadius: 8,
    padding: 12,
    fontSize: 16,
  },
  textArea: {
    minHeight: 100,
    textAlignVertical: 'top',
  },
  recordButton: {
    marginTop: 8,
    padding: 12,
    borderRadius: 8,
    backgroundColor: '#f0f0f0',
    alignItems: 'center',
  },
  recordButtonText: {
    color: '#007AFF',
    fontSize: 16,
  },
  submitButton: {
    backgroundColor: '#007AFF',
    padding: 16,
    borderRadius: 8,
    alignItems: 'center',
    marginTop: 20,
  },
  submitButtonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: 'bold',
  },
});

export default NewIncidentScreen; 