import React, { useState } from 'react';
import { View, Text, StyleSheet, FlatList, TouchableOpacity, TextInput } from 'react-native';
import { useNavigation } from '@react-navigation/native';

type Incident = {
  id: string;
  title: string;
  date: string;
  status: string;
  description: string;
};

const IncidentsScreen = () => {
  const navigation = useNavigation();
  const [searchQuery, setSearchQuery] = useState('');
  const [filterStatus, setFilterStatus] = useState('all');

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
      onPress={() => navigation.navigate('IncidentDetails', { incident: item })}
    >
      <Text style={styles.incidentTitle}>{item.title}</Text>
      <Text style={styles.incidentDate}>{item.date}</Text>
      <Text style={[
        styles.incidentStatus,
        { color: item.status === 'En cours' ? '#FF9500' : '#34C759' }
      ]}>
        {item.status}
      </Text>
    </TouchableOpacity>
  );

  return (
    <View style={styles.container}>
      <View style={styles.searchContainer}>
        <TextInput
          style={styles.searchInput}
          placeholder="Rechercher un incident..."
          value={searchQuery}
          onChangeText={setSearchQuery}
        />
        <View style={styles.filterContainer}>
          <TouchableOpacity 
            style={[styles.filterButton, filterStatus === 'all' && styles.activeFilter]}
            onPress={() => setFilterStatus('all')}
          >
            <Text style={styles.filterText}>Tous</Text>
          </TouchableOpacity>
          <TouchableOpacity 
            style={[styles.filterButton, filterStatus === 'En cours' && styles.activeFilter]}
            onPress={() => setFilterStatus('En cours')}
          >
            <Text style={styles.filterText}>En cours</Text>
          </TouchableOpacity>
          <TouchableOpacity 
            style={[styles.filterButton, filterStatus === 'Résolu' && styles.activeFilter]}
            onPress={() => setFilterStatus('Résolu')}
          >
            <Text style={styles.filterText}>Résolus</Text>
          </TouchableOpacity>
        </View>
      </View>

      <FlatList
        data={filteredIncidents}
        renderItem={renderIncidentItem}
        keyExtractor={item => item.id}
        style={styles.list}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
  searchContainer: {
    padding: 16,
    backgroundColor: '#f5f5f5',
  },
  searchInput: {
    backgroundColor: '#fff',
    padding: 10,
    borderRadius: 8,
    marginBottom: 10,
  },
  filterContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
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
    color: '#000',
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
  },
  incidentDate: {
    color: '#666',
    marginTop: 4,
  },
  incidentStatus: {
    marginTop: 4,
    fontWeight: '500',
  },
});

export default IncidentsScreen; 