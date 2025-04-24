import React, { useState } from 'react';
import { View, StyleSheet, TextInput, TouchableOpacity, ScrollView, GestureResponderEvent } from 'react-native';
import { SafeArea } from '@/components/SafeArea';
import { ThemedText } from '@/components/ThemedText';
import { Colors } from '@/constants/Colors';
import { useColorScheme } from '@/hooks/useColorScheme';
import { IconSymbol } from '@/components/ui/IconSymbol';
import { useTheme } from '@react-navigation/native';

const AccountScreen = () => {
  const [feedback, setFeedback] = useState('');
  const [isConnected, _setIsConnected] = useState(true); // This would come from NetInfo
  const colorScheme = useColorScheme();

  const handleSubmitFeedback = () => {
    // Here we would send the feedback to the server
    console.log('Feedback submitted:', feedback);
    setFeedback('');
  };

  const handleLogout = () => {
    // Here we would handle the logout logic
    console.log('Logout');
  };

  function toggleTheme(event: GestureResponderEvent): void {
    throw new Error('Function not implemented.');
  }

  return (
    <SafeArea>
      <ScrollView style={styles.container}>
        <View style={styles.header}>
          <ThemedText style={styles.title}>Mon Compte</ThemedText>
          <View style={styles.headerRight}>
            <TouchableOpacity
              style={styles.themeButton}
              onPress={toggleTheme}
            >
              <IconSymbol
                name={'exclamationmark.triangle.fill'}
                size={24}
                color={Colors[useColorScheme() ?? 'light'].text}
              />
            </TouchableOpacity>
            <View style={styles.connectionStatus}>
              <View style={[
                styles.statusDot,
                { backgroundColor: isConnected ? '#34C759' : '#FF3B30' }
              ]} />
              <ThemedText style={styles.statusText}>
                {isConnected ? 'Connecté' : 'Hors ligne'}
              </ThemedText>
            </View>
          </View>
        </View>

        <View style={styles.section}>
          <ThemedText style={styles.sectionTitle}>Informations personnelles</ThemedText>
          <View style={styles.infoItem}>
            <ThemedText style={styles.infoLabel}>Nom</ThemedText>
            <ThemedText style={styles.infoValue}>Dupont</ThemedText>
          </View>
          <View style={styles.infoItem}>
            <ThemedText style={styles.infoLabel}>Prénom</ThemedText>
            <ThemedText style={styles.infoValue}>Jean</ThemedText>
          </View>
          <View style={styles.infoItem}>
            <ThemedText style={styles.infoLabel}>Matricule</ThemedText>
            <ThemedText style={styles.infoValue}>12345</ThemedText>
          </View>
        </View>

        <View style={styles.section}>
          <ThemedText style={styles.sectionTitle}>Suggestions d'amélioration</ThemedText>
          <TextInput
            style={[
              styles.feedbackInput,
              {
                color: Colors[useColorScheme() ?? 'light'].text,
                backgroundColor: Colors[useColorScheme() ?? 'light'].background
              }
            ]}
            placeholder="Vos suggestions pour améliorer l'application..."
            placeholderTextColor={Colors[useColorScheme() ?? 'light'].icon}
            value={feedback}
            onChangeText={setFeedback}
            multiline
            numberOfLines={4}
          />
          <TouchableOpacity
            style={styles.submitButton}
            onPress={handleSubmitFeedback}
          >
            <ThemedText style={styles.submitButtonText}>Envoyer</ThemedText>
          </TouchableOpacity>
        </View>

        <TouchableOpacity
          style={styles.logoutButton}
          onPress={handleLogout}
        >
          <ThemedText style={styles.logoutButtonText}>Se déconnecter</ThemedText>
        </TouchableOpacity>
      </ScrollView>
    </SafeArea>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
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
  headerRight: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 16,
  },
  themeButton: {
    padding: 8,
    borderRadius: 8,
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