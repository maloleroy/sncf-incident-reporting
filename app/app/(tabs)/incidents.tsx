import React, { useState } from 'react';
import { View, StyleSheet, FlatList, TouchableOpacity, TextInput } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { NativeStackNavigationProp } from '@react-navigation/native-stack';
import { SafeArea } from '@/components/SafeArea';
import { ThemedText } from '@/components/ThemedText';
import { Colors } from '@/constants/Colors';
import { useColorScheme } from '@/hooks/useColorScheme';

type RootStackParamList = {
  'new-incident': undefined;
  'incident-details': { incident: Incident };
};

type NavigationProp = NativeStackNavigationProp<RootStackParamList>;

type Incident = {
  id: string;
  title: string;
  date: string;
  status: string;
  description: string;
};

const IncidentsScreen = () => {
  const navigation = useNavigation<NavigationProp>();
  const [searchQuery, setSearchQuery] = useState('');
  const [filterStatus, setFilterStatus] = useState('all');
  const colorScheme = useColorScheme();

  // Temporary mock data
  const incidents: Incident[] = [
    {
      id: '1',
      title: 'Retard sur la ligne A',
      date: '2024-04-21',
      status: 'En cours',
      description: 'Retard de 30 minutes sur la ligne A due à un problème technique'
    },
    {
      id: '2',
      title: 'Problème technique',
      date: '2024-04-20',
      status: 'Résolu',
      description: 'Problème de signalisation résolu'
    },
    {
      id: '3',
      title: 'Incident passager',
      date: '2024-04-19',
      status: 'En cours',
      description: 'Conflit entre passagers'
    },
  ];

  const filteredIncidents = incidents.filter(incident => {
    const matchesSearch = incident.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
      incident.description.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesStatus = filterStatus === 'all' || incident.status === filterStatus;
    return matchesSearch && matchesStatus;
  });

  const renderIncidentItem = ({ item }: { item: Incident }) => (
    <TouchableOpacity
      style={styles.incidentItem}
      onPress={() => navigation.navigate('incident-details', { incident: item })}
    >
      <ThemedText style={styles.incidentTitle}>{item.title}</ThemedText>
      <ThemedText style={styles.incidentDate}>{item.date}</ThemedText>
      <ThemedText style={[
        styles.incidentStatus,
        { color: item.status === 'En cours' ? '#FF9500' : '#34C759' }
      ]}>
        {item.status}
      </ThemedText>
    </TouchableOpacity>
  );

  return (
    <SafeArea>
      <View style={styles.container}>
        <View style={styles.searchContainer}>
          <TextInput
            style={[
              styles.searchInput,
              {
                color: Colors[colorScheme ?? 'light'].text,
                backgroundColor: Colors[colorScheme ?? 'light'].background
              }
            ]}
            placeholder="Rechercher un incident..."
            placeholderTextColor={Colors[colorScheme ?? 'light'].icon}
            value={searchQuery}
            onChangeText={setSearchQuery}
          />
          <View style={styles.filterContainer}>
            <TouchableOpacity
              style={[styles.filterButton, filterStatus === 'all' && styles.activeFilter]}
              onPress={() => setFilterStatus('all')}
            >
              <ThemedText style={styles.filterText}>Tous</ThemedText>
            </TouchableOpacity>
            <TouchableOpacity
              style={[styles.filterButton, filterStatus === 'En cours' && styles.activeFilter]}
              onPress={() => setFilterStatus('En cours')}
            >
              <ThemedText style={styles.filterText}>En cours</ThemedText>
            </TouchableOpacity>
            <TouchableOpacity
              style={[styles.filterButton, filterStatus === 'Résolu' && styles.activeFilter]}
              onPress={() => setFilterStatus('Résolu')}
            >
              <ThemedText style={styles.filterText}>Résolus</ThemedText>
            </TouchableOpacity>
          </View>
        </View>

        <TouchableOpacity
          style={styles.newIncidentButton}
          onPress={() => navigation.navigate('new-incident')}
        >
          <ThemedText style={styles.newIncidentButtonText}>Nouvel Incident</ThemedText>
        </TouchableOpacity>

        <FlatList
          data={filteredIncidents}
          renderItem={renderIncidentItem}
          keyExtractor={item => item.id}
          style={styles.list}
        />
      </View>
    </SafeArea>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  searchContainer: {
    padding: 16,
  },
  searchInput: {
    borderWidth: 1,
    borderRadius: 8,
    padding: 12,
    marginBottom: 12,
  },
  filterContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 16,
  },
  filterButton: {
    padding: 8,
    borderRadius: 4,
    backgroundColor: '#e0e0e0',
  },
  activeFilter: {
    backgroundColor: '#007AFF',
  },
  filterText: {
    fontSize: 14,
  },
  newIncidentButton: {
    backgroundColor: '#007AFF',
    padding: 16,
    margin: 16,
    borderRadius: 8,
    alignItems: 'center',
  },
  newIncidentButtonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: 'bold',
  },
  list: {
    flex: 1,
  },
  incidentItem: {
    padding: 16,
    borderBottomWidth: 1,
    borderBottomColor: '#e0e0e0',
  },
  incidentTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 4,
  },
  incidentDate: {
    fontSize: 14,
    marginBottom: 4,
  },
  incidentStatus: {
    fontSize: 14,
    fontWeight: '500',
  },
});

export default IncidentsScreen;
