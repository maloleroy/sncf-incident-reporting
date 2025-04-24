import React, { useState } from 'react';
import { View, Text, StyleSheet, FlatList, TouchableOpacity, TextInput } from 'react-native';
import { SafeArea } from '@/components/SafeArea';

type Trip = {
  id: string;
  route: string;
  date: string;
  status: string;
  trainNumber: string;
};

const TripsScreen = () => {
  const [searchQuery, setSearchQuery] = useState('');
  const [filterStatus, setFilterStatus] = useState('all');

  // Temporary mock data
  const trips: Trip[] = [
    {
      id: '1',
      route: 'Paris - Lyon',
      date: '2024-04-21',
      status: 'Terminé',
      trainNumber: 'TGV 1234'
    },
    {
      id: '2',
      route: 'Lyon - Marseille',
      date: '2024-04-20',
      status: 'En cours',
      trainNumber: 'TGV 5678'
    },
    {
      id: '3',
      route: 'Marseille - Nice',
      date: '2024-04-19',
      status: 'Terminé',
      trainNumber: 'TGV 9012'
    },
  ];

  const filteredTrips = trips.filter(trip => {
    const matchesSearch = trip.route.toLowerCase().includes(searchQuery.toLowerCase()) ||
      trip.trainNumber.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesStatus = filterStatus === 'all' || trip.status === filterStatus;
    return matchesSearch && matchesStatus;
  });

  const renderTripItem = ({ item }: { item: Trip }) => (
    <View style={styles.tripItem}>
      <View style={styles.tripHeader}>
        <Text style={styles.trainNumber}>{item.trainNumber}</Text>
        <Text style={[
          styles.tripStatus,
          { color: item.status === 'En cours' ? '#FF9500' : '#34C759' }
        ]}>
          {item.status}
        </Text>
      </View>
      <Text style={styles.tripRoute}>{item.route}</Text>
      <Text style={styles.tripDate}>{item.date}</Text>
    </View>
  );

  return (
    <SafeArea>
      <View style={styles.container}>
        <View style={styles.searchContainer}>
          <TextInput
            style={styles.searchInput}
            placeholder="Rechercher un trajet..."
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
              style={[styles.filterButton, filterStatus === 'Terminé' && styles.activeFilter]}
              onPress={() => setFilterStatus('Terminé')}
            >
              <Text style={styles.filterText}>Terminés</Text>
            </TouchableOpacity>
          </View>
        </View>

        <FlatList
          data={filteredTrips}
          renderItem={renderTripItem}
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
  tripItem: {
    padding: 16,
    borderBottomWidth: 1,
    borderBottomColor: '#e0e0e0',
  },
  tripHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 8,
  },
  trainNumber: {
    fontSize: 16,
    fontWeight: 'bold',
  },
  tripStatus: {
    fontWeight: '500',
  },
  tripRoute: {
    fontSize: 16,
    marginBottom: 4,
  },
  tripDate: {
    color: '#666',
  },
});

export default TripsScreen;
