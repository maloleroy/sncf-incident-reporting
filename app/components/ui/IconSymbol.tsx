// This file is a fallback for using MaterialIcons on Android and web.

import { MaterialIcons } from '@expo/vector-icons';
import { SymbolWeight } from 'expo-symbols';
import React from 'react';
import { StyleProp, TextStyle } from 'react-native';
import { IconName } from '../types';

type MaterialIconName = React.ComponentProps<typeof MaterialIcons>['name'];

// Add your SFSymbol to MaterialIcons mappings here.
const MAPPING: Record<IconName, MaterialIconName> = {
  // See MaterialIcons here: https://icons.expo.fyi
  // See SF Symbols in the SF Symbols app on Mac.
  'house.fill': 'home',
  'paperplane.fill': 'send',
  'chevron.left.forwardslash.chevron.right': 'code',
  'chevron.right': 'chevron-right',
  'exclamationmark.triangle.fill': 'warning',
  'train.side.front.car': 'train',
  'person.fill': 'person',
};

export function IconSymbol({
  name,
  size = 24,
  color = 'black',
  style,
}: {
  name: IconName;
  size?: number;
  color?: string;
  style?: StyleProp<TextStyle>;
  weight?: SymbolWeight;
}) {
  return <MaterialIcons color={color} size={size} name={MAPPING[name]} style={style} />;
}
