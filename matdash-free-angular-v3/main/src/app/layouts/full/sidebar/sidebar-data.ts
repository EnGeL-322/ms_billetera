import { NavItem } from './nav-item/nav-item';

export const navItems: NavItem[] = [
  {
    navCap: 'Menú',
  },

  {
    displayName: 'Billetera',
    iconName: 'solar:wallet-2-line-duotone',
    route: '/app/ui-components/wallet'
  },

  {
    displayName: 'Transacciones',
    iconName: 'solar:bill-line-duotone',
    route: '/app/ui-components/transactions'
  },

  {
    displayName: 'Eventos',
    iconName: 'solar:calendar-mark-line-duotone',
    route: '/app/ui-components/events'
  },

  {
    displayName: 'Objetivos',
    iconName: 'solar:target-line-duotone',
    route: '/app/ui-components/goals'
  }
  ,
  {
  displayName: 'Categorías',
  iconName: 'solar:layers-minimalistic-bold-duotone',
  route: '/app/ui-components/menu'
},




  
];
