import { Link, type Href } from 'expo-router';
import { Text, StyleSheet } from 'react-native';
import { useTheme } from '@react-navigation/native';

interface ExternalLinkProps {
  href: Href;
  children: React.ReactNode;
}

export function ExternalLink({ href, children }: ExternalLinkProps) {
  const { colors } = useTheme();
  return (
    <Link href={href} style={[styles.link, { color: colors.primary }]}>
      <Text>{children}</Text>
    </Link>
  );
}

const styles = StyleSheet.create({
  link: {
    textDecorationLine: 'underline',
  },
});
