import { SafeAreaView as RNSafeAreaView } from 'react-native';
import { Platform } from 'react-native';

export function SafeArea({ children }: { children: React.ReactNode }) {
  return (
    <RNSafeAreaView style={{ 
      flex: 1,
      paddingTop: Platform.OS === 'android' ? 50 : 0 
    }}>
      {children}
    </RNSafeAreaView>
  );
} 