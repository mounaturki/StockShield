export interface User {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
  warehouseId?: number;
  enabled?: boolean;
}

export interface AuthResponse {
  token: string;
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
}

export interface Client {
  id: number;
  name: string;
  address: string;
  phone: string;
  manager: string;
  contractPath?: string;
  contractExpiry: string;
  active: boolean;
}

export interface Machine {
  id: number;
  serialNumber: string;
  model: string;
  machineType: string;
  machineTypeLabel: string;
  clientId: number;
  clientName: string;
  status: string;
  installationDate: string;
  lastMaintenance: string;
  nextMaintenance: string;
  notes?: string;
}

export interface Product {
  id: number;
  name: string;
  category: string;
  categoryLabel?: string;
  periodCode?: string;
  price: number;
  currentStock: number;
  minimumStock: number;
  supplier: string;
  description?: string;
  active: boolean;
  lowStock: boolean;
}

export interface StockMovement {
  id: number;
  productId: number;
  productName: string;
  type: string;
  quantity: number;
  reason: string;
  performedBy: string;
  createdAt: string;
}

export interface Alert {
  id: number;
  type: string;
  severity: string;
  title: string;
  message: string;
  read: boolean;
  resolved: boolean;
  createdAt: string;
}

export interface Dashboard {
  totalClients: number;
  totalMachines: number;
  totalProducts: number;
  activeAlerts: number;
  failedLogins: number;
  lowStockProducts: number;
  inventoryDiscrepancies: number;
  recentAlerts: Alert[];
}

export interface RiskItem {
  id?: number;
  title: string;
  description: string;
  severity: string;
  entityType: string;
  entityId?: number;
}

export interface RiskCenter {
  riskyUsers: RiskItem[];
  criticalProducts: RiskItem[];
  maintenanceMachines: RiskItem[];
  expiringContracts: RiskItem[];
  inventoryAnomalies: RiskItem[];
}

export interface AuditLog {
  id: number;
  username: string;
  action: string;
  entityType: string;
  entityId: number;
  oldValue: string;
  newValue: string;
  createdAt: string;
}

export interface Discount {
  id: number;
  name: string;
  percentage: number;
  conditions: string;
  active: boolean;
  validated: boolean;
  validatedBy?: string;
}

export interface Inventory {
  id: number;
  type: string;
  inventoryDate: string;
  performedBy: string;
  status: string;
  totalDiscrepancies: number;
}

export interface StockRisk {
  criticalProducts: RiskItem[];
  inventoryAnomalies: RiskItem[];
  stockAlerts: RiskItem[];
  lowStockCount: number;
  discrepancyCount: number;
}

export interface ProductPeriod {
  id: number;
  code: string;
  name: string;
  startDate: string;
  endDate: string;
  active: boolean;
}
