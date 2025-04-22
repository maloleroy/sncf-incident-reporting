import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { SafeAreaProvider } from 'react-native-safe-area-context';

// Import screens (we'll create these next)
import HomeScreen from './src/screens/HomeScreen';
import IncidentsScreen from './src/screens/IncidentsScreen';
import TripsScreen from './src/screens/TripsScreen';
import AccountScreen from './src/screens/AccountScreen';
import NewIncidentScreen from './src/screens/NewIncidentScreen';

const Tab = createBottomTabNavigator();
const Stack = createNativeStackNavigator();

function TabNavigator() {
  return (
    <Tab.Navigator>
      <Tab.Screen name="Accueil" component={HomeScreen} />
      <Tab.Screen name="Incidents" component={IncidentsScreen} />
      <Tab.Screen name="Trajets" component={TripsScreen} />
      <Tab.Screen name="Compte" component={AccountScreen} />
    </Tab.Navigator>
  );
}

export default function App() {
  return (
    <SafeAreaProvider>
      <NavigationContainer>
        <Stack.Navigator>
          <Stack.Screen 
            name="MainTabs" 
            component={TabNavigator} 
            options={{ headerShown: false }}
          />
          <Stack.Screen 
            name="NewIncident" 
            component={NewIncidentScreen}
            options={{ title: 'Nouvel Incident' }}
          />
        </Stack.Navigator>
      </NavigationContainer>
    </SafeAreaProvider>
  );
} 