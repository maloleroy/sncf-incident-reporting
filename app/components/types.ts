export type IconName = 
  | 'house.fill'
  | 'paperplane.fill'
  | 'chevron.left.forwardslash.chevron.right'
  | 'chevron.right'
  | 'exclamationmark.triangle.fill'
  | 'train.side.front.car'
  | 'person.fill';

export interface Incident {
  id: string;
  title: string;
  description: string;
  date: string;
  location: string;
  severity: 'low' | 'medium' | 'high';
  status: 'open' | 'in_progress' | 'resolved';
  feedback?: Feedback;
}

export interface Trip {
  id: string;
  origin: string;
  destination: string;
  date: string;
  incidents: Incident[];
}

export interface Feedback {
  id: string;
  incidentId: string;
  rating: number;
  comment?: string;
  date: string;
} 