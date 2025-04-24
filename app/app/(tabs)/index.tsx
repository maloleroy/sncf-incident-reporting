import React from 'react';
import { View, Text, StyleSheet, FlatList, TouchableOpacity } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { NativeStackNavigationProp } from '@react-navigation/native-stack';
import { SafeArea } from '@/components/SafeArea';

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
};

type Trip = {
  id: string;
  route: string;
  date: string;
};

const HomeScreen = () => {
  const navigation = useNavigation<NavigationProp>();

  // Temporary mock data
  const recentIncidents: Incident[] = [
    { id: '1', title: 'Retard sur la ligne A', date: '2024-04-21', status: 'En cours' },
    { id: '2', title: 'Problème technique', date: '2024-04-20', status: 'Résolu' },
    { id: '3', title: 'Problème technique 2', date: '2024-04-20', status: 'Delayed' },
  ];

  const recentTrips: Trip[] = [
    { id: '1', route: 'Paris - Lyon', date: '2024-04-21' },
    { id: '2', route: 'Lyon - Marseille', date: '2024-04-20' },
  ];

  const renderIncidentItem = ({ item }: { item: Incident }) => (
    <View style={styles.incidentItem}>
      <Text style={styles.incidentTitle}>{item.title}</Text>
      <Text style={styles.incidentDate}>{item.date}</Text>
      <Text style={styles.incidentStatus}>{item.status}</Text>
    </View>
  );

  const renderTripItem = ({ item }: { item: Trip }) => (
    <View style={styles.tripItem}>
      <Text style={styles.tripRoute}>{item.route}</Text>
      <Text style={styles.tripDate}>{item.date}</Text>
    </View>
  );

  return (
    <SafeArea>
      <View style={styles.container}>
        <TouchableOpacity
          style={styles.addButton}
          onPress={() => navigation.navigate('new-incident')}
        >
          <Text style={styles.addButtonText}>Ajouter un incident</Text>
        </TouchableOpacity>

        <Text style={styles.sectionTitle}>Incidents récents</Text>
        <FlatList
          data={recentIncidents}
          renderItem={renderIncidentItem}
          keyExtractor={item => item.id}
          style={styles.list}
        />

        <Text style={styles.sectionTitle}>Trajets récents</Text>
        <FlatList
          data={recentTrips}
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
    padding: 16,
  },
  addButton: {
    backgroundColor: '#007AFF',
    padding: 15,
    borderRadius: 8,
    alignItems: 'center',
    marginBottom: 20,
  },
  addButtonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: 'bold',
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 10,
  },
  list: {
    marginBottom: 20,
  },
  incidentItem: {
    backgroundColor: '#f5f5f5',
    padding: 15,
    borderRadius: 8,
    marginBottom: 10,
  },
  incidentTitle: {
    fontSize: 16,
    fontWeight: 'bold',
  },
  incidentDate: {
    color: '#666',
    marginTop: 5,
  },
  incidentStatus: {
    color: '#007AFF',
    marginTop: 5,
  },
  tripItem: {
    backgroundColor: '#f5f5f5',
    padding: 15,
    borderRadius: 8,
    marginBottom: 10,
  },
  tripRoute: {
    fontSize: 16,
    fontWeight: 'bold',
  },
  tripDate: {
    color: '#666',
    marginTop: 5,
  },
});

export default HomeScreen; 