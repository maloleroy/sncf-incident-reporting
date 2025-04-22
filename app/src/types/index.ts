export type Incident = {
  id: string;
  title: string;
  description: string;
  location: string;
  date: string;
  status: 'En cours' | 'Résolu';
  trainNumber?: string;
  userId: string;
};

export type Trip = {
  id: string;
  route: string;
  date: string;
  status: 'En cours' | 'Terminé';
  trainNumber: string;
  userId: string;
};

export type User = {
  id: string;
  firstName: string;
  lastName: string;
  employeeId: string;
};

export type Feedback = {
  id: string;
  userId: string;
  content: string;
  date: string;
  status: 'Nouveau' | 'En cours' | 'Traité';
}; 