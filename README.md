# ELP Compliance Manager

A production-grade Java Spring Boot backend that automates the **Microsoft License Compliance Verification (LCV)** process — from raw asset data collection across multiple sources, through normalization and coverage analysis, to gap calculation and formal ELP (Effective License Position) report generation.

Built as a portfolio project by someone working in Microsoft License Contract Compliance at EY, replicating the real-world engagement workflow.

**GitHub:** [github.com/shobhit1502/elp-compliance-manager](https://github.com/shobhit1502/elp-compliance-manager)

---

## Table of Contents

- [Problem Statement](#problem-statement)
- [What This System Does](#what-this-system-does)
- [Complete Flow](#complete-flow)
- [Domain Knowledge](#domain-knowledge)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Module Breakdown](#module-breakdown)
- [Connector Module](#connector-module)
- [Normalization Layer](#normalization-layer)
- [Coverage Analysis and Extrapolation](#coverage-analysis-and-extrapolation)
- [Compliance Engine](#compliance-engine)
- [Licensing Rules Engine](#licensing-rules-engine)
- [ELP Report](#elp-report)
- [API Reference](#api-reference)
- [Database Schema](#database-schema)
- [Running Locally](#running-locally)
- [Sample Data](#sample-data)
- [Interview Talking Points](#interview-talking-points)

---

## Problem Statement

Enterprise companies using Microsoft software must periodically prove their deployments are properly licensed through a process called **License Compliance Verification (LCV)**. This process is currently done manually:

```
Collect asset data from multiple sources
        ↓
Build completeness workbook (coverage analysis)
        ↓
Normalize and deduplicate across sources
        ↓
Receive Microsoft License Statement (MLS)
        ↓
Apply licensing rules (downgrade, SA rights etc.)
        ↓
Calculate gap per product (deployed vs licensed)
        ↓
Produce formal ELP Excel report
```

Every step above is done manually in Excel across multiple workbooks. This system automates the entire pipeline through a structured REST API.

---

## What This System Does

- Connects to multiple asset sources (AD, SCCM, VMware, ServiceNow) via pluggable connectors
- Normalizes raw data from each source into a standard asset model
- Deduplicates machines appearing across multiple sources
- Calculates coverage percentage and extrapolation factor
- Ingests license entitlements (Microsoft License Statement)
- Applies Microsoft licensing rules (version downgrade rights, Software Assurance upgrade rights)
- Calculates compliance gap per product using correct extrapolation logic
- Generates downloadable ELP Excel report with 5 tabs
- Maintains audit trail of all actions
- Supports multiple client companies in the same system

---

## Complete Flow

```
┌─────────────────────────────────────────────────────┐
│              CLIENT ENVIRONMENT                      │
│  Active Directory  SCCM  VMware  ServiceNow CMDB    │
└────────────────────┬────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────┐
│                CONNECTOR LAYER                       │
│  ADConnector  SCCMConnector  VMwareConnector         │
│  ServiceNowConnector                                 │
│  (each calls its source, fetches raw data)           │
└────────────────────┬────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────┐
│             NORMALIZATION LAYER                      │
│  Field mapping (device_name → machineName)           │
│  OS normalization (Win10 → Windows 10 Enterprise)    │
│  Type normalization (Desktop → WORKSTATION)          │
│  Software normalization (MS Office → Office|2019)    │
└────────────────────┬────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────┐
│                DEDUPLICATION                         │
│  Same machine from AD + SCCM → 1 record             │
│  Same software found twice → 1 deployment record    │
└────────────────────┬────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────┐
│              ASSET DATABASE                          │
│  assets table → machines, OS, coverage tag          │
│  asset_deployments → software per machine           │
│  virtual_machines → VM → Host → Cluster mapping     │
└────────────────────┬────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────┐
│            COVERAGE ANALYSIS                         │
│  Covered assets (hasScriptOutput = true)            │
│  Uncovered assets (AD/VMware only)                  │
│  Coverage % = covered / total × 100                 │
└────────────────────┬────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────┐
│           ENTITLEMENT INGESTION                      │
│  Upload Microsoft License Statement CSV             │
│  Product mapping, license type, SA flag             │
└────────────────────┬────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────┐
│            COMPLIANCE ENGINE                         │
│                                                     │
│  OS Products (Windows, Windows Server):             │
│    Count from ALL assets (AD/VMware give OS)        │
│    No extrapolation needed                          │
│                                                     │
│  Software Products (SQL, Office, Visio etc.):       │
│    Count from COVERED assets only                   │
│    Rate = deployed / covered machines               │
│    Extrapolated = rate × uncovered machines         │
│    Total = covered count + extrapolated             │
│                                                     │
│  Apply Licensing Rules:                             │
│    Version Downgrade Rights                         │
│    Software Assurance Upgrade Rights                │
│                                                     │
│  Gap = Effective Licensed - Extrapolated Deployed   │
│  Status = COMPLIANT / UNDER_LICENSED / OVER_LICENSED│
└────────────────────┬────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────┐
│              ELP REPORT (Excel)                      │
│  Tab 1: Summary                                     │
│  Tab 2: Coverage Analysis                           │
│  Tab 3: Asset Register                              │
│  Tab 4: Compliance Results                          │
│  Tab 5: Entitlements                               │
└─────────────────────────────────────────────────────┘
```

---

## Domain Knowledge

### What is an ELP?

An **Effective License Position (ELP)** is the formal deliverable of a Microsoft License Compliance Verification engagement. It shows, for every Microsoft product deployed in a client's environment:

- How many machines it is deployed on (extrapolated)
- How many licenses the client owns
- The gap (positive = over-licensed, negative = under-licensed)

### Asset Baselining

Before calculating compliance, you must build a complete picture of the client's infrastructure:

```
Source              What it provides
─────────────────────────────────────────────────
Active Directory    Machine names, OS, domain
SCCM                Installed software per machine
VMware Export       VM → Host → Cluster topology
ServiceNow CMDB     Asset ownership, department
EY Script Output    Detailed software inventory
```

### Coverage and Extrapolation

You can never collect data from 100% of assets. Some machines are offline, decommissioned, or inaccessible. The **extrapolation** accounts for uncovered machines:

```
Example:
  Total assets:     1000
  Covered assets:    800  (script collected)
  Uncovered assets:  200

  Office found in covered: 400 machines
  Deployment rate = 400/800 = 50%

  Estimated on uncovered = 50% × 200 = 100

  Final Office deployment = 400 + 100 = 500
```

**Key rule:** OS products do not need extrapolation because AD/VMware provide OS for ALL machines. Software products need extrapolation because only SCCM/scripts provide software inventory.

### What Makes a Machine "Covered"?

```
hasScriptOutput = true  → covered
  Machine has detailed software inventory
  Came from SCCM, EY script, or ServiceNow with software

hasScriptOutput = false → uncovered
  Machine exists but no software detail
  Came from AD only or VMware only
```

### Microsoft Licensing Rules Applied

**Version Downgrade Rights:**
A license for a newer version covers older version deployments of the same edition.
```
Windows Server 2022 Standard license
  → covers Windows Server 2019 Standard deployment
  → does NOT cover Windows Server 2019 Datacenter
```

**Software Assurance Upgrade Rights:**
An older license with SA flag covers newer version deployments.
```
SQL Server 2016 Standard + SA
  → covers SQL Server 2019 Standard deployment
```

---

## Architecture

```
Controller Layer    REST endpoints, request/response handling
      ↓
Service Layer       Business logic, compliance rules, orchestration
      ↓
Repository Layer    Spring Data JPA queries
      ↓
PostgreSQL          Persistent storage
```

Package structure (package-by-feature):

```
com.elp.compliance_manager
├── auth/               JWT login, register, token validation
├── company/            Client company management
├── product/            Microsoft product catalog (24 products)
├── asset/              Asset entity, CSV ingestion
├── coverage/           Coverage analysis and extrapolation
├── virtualization/     VM → Host → Cluster topology
├── entitlement/        License entitlement management
├── compliance/         Compliance engine and rules
│   └── rules/          LicensingRule interface + implementations
├── deployment/         Asset software deployment records
├── connector/          Pluggable connector architecture
│   ├── impl/           AD, SCCM, VMware, ServiceNow connectors
│   └── mock/           Mock data sources (simulates real systems)
├── report/             ELP Excel report generator
├── audit/              Audit trail logging
└── security/           JWT filter, security configuration
```

---

## Tech Stack

| Component | Technology |
|---|---|
| Language | Java 25 |
| Framework | Spring Boot 3.5.14 |
| Database | PostgreSQL 15 |
| ORM | Spring Data JPA + Hibernate |
| Security | Spring Security + JWT (jjwt 0.11.5) |
| Excel Generation | Apache POI 5.2.3 |
| CSV Parsing | OpenCSV 5.7.1 |
| API Docs | SpringDoc OpenAPI 2.8.9 (Swagger UI) |
| Build | Maven |
| Dev Tools | Lombok, Spring DevTools |

---

## Module Breakdown

### Auth Module
- User registration and login
- JWT token generation and validation (7 day expiry for dev)
- BCrypt password hashing
- Roles: ADMIN, AUDITOR, REVIEWER
- All endpoints protected except `/api/auth/**`

### Company Module
- Create and manage client companies
- Status: ACTIVE, INACTIVE, COMPLETED
- Full CRUD with validation

### Product Catalog
- 24 Microsoft products auto-seeded on startup via DataSeeder
- Categories: DESKTOP_OS, SERVER_OS, DATABASE, MESSAGING, PRODUCTIVITY, DEVELOPER_TOOLS
- Each product has name, version, edition

### Asset Module
- Asset entity: machineName, operatingSystem, assetType, domain, hasScriptOutput, assetSource
- Legacy CSV upload endpoint (Phase 4)
- New connector-based ingestion (Phase 11)
- Asset types: WORKSTATION, SERVER, VIRTUAL_MACHINE, UNKNOWN

### Coverage Module
- Calculates: totalAssets, coveredAssets, uncoveredAssets, coveragePercentage
- Breaks down by workstation and server
- Coverage status: EXCELLENT (>90%), GOOD (>75%), ACCEPTABLE (>60%), INSUFFICIENT (<60%)
- Extrapolation factor = totalAssets / coveredAssets

### Virtualization Module
- Ingests VMware exports (CSV or connector)
- Builds VM → Host → Cluster mapping
- Returns topology with host summaries and vCPU counts
- Used for Windows Server and SQL Server virtualization licensing

### Entitlement Module
- Bulk CSV ingestion simulating Microsoft License Statement
- Fields: productId, quantity, licenseType, licenseMetric, hasSoftwareAssurance, source
- License types: OEM, VOLUME, EA, MPSA, RETAIL
- License metrics: PER_DEVICE, PER_USER, PER_CORE, SERVER_CAL

---

## Connector Module

The connector module simulates how enterprise SAM tools (ServiceNow, Flexera, Snow) collect inventory from multiple systems.

### Architecture

```
DataConnector (interface)
      ↓
ADConnector          → fetches machine list, OS, domain
SCCMConnector        → fetches installed software per machine
VMwareConnector      → fetches VM topology
ServiceNowConnector  → fetches CMDB data with ownership
      ↓
MockDataSource       → simulates real external systems
(returns realistic JSON per company)
      ↓
ConnectorOrchestrator → deduplication + saving to DB
```

### How Each Connector Works

**ADConnector:**
```
Calls mockDataSource.getADData(companyId)
Receives: [{computerName, operatingSystem, deviceType, domain, lastLogon}]
Maps to:   machineName, normalizedOS, assetType, domain, lastSeen
Sets:      hasScriptOutput = false (AD has no software detail)
```

**SCCMConnector:**
```
Calls mockDataSource.getSCCMData(companyId)
Receives: [{device_name, os, device_type, installed_software[]}]
Maps to:   machineName, normalizedOS + software deployments
Sets:      hasScriptOutput = true (SCCM has software inventory)
Updates:   existing asset hasScriptOutput → true if found in SCCM
```

**VMwareConnector:**
```
Calls mockDataSource.getVMwareData(companyId)
Receives: [{vm_name, host_name, cluster_name, num_cpu, guest_os}]
Maps to:   machineName as VIRTUAL_MACHINE + virtualization topology
Sets:      hasScriptOutput = false (VMware has no software detail)
```

**ServiceNowConnector:**
```
Calls mockDataSource.getServiceNowData(companyId)
Receives: [{u_hostname, u_os, u_classification, u_department, u_installed_software[]}]
Maps to:   machineName, normalizedOS + software deployments
Sets:      hasScriptOutput = true if software found
```

### Sync Endpoints

```
POST /api/connectors/sync/{companyId}/AD_EXPORT
POST /api/connectors/sync/{companyId}/SCCM_EXPORT
POST /api/connectors/sync/{companyId}/VMWARE_EXPORT
POST /api/connectors/sync/{companyId}/SERVICENOW_EXPORT
POST /api/connectors/sync-all/{companyId}   ← runs all 4 at once
```

### Multi-Company Support

MockDataSource returns different data per companyId:
- companyId = 1 → Contoso Technologies Pvt Ltd data
- companyId = 3 → Tailwind Traders Pvt Ltd data

---

## Normalization Layer

Every source uses different field names and value formats for the same data. The normalization layer converts everything to our standard internal format.

### OS Normalization

```
Source          Raw Value                    Normalized
─────────────────────────────────────────────────────────
AD              "Windows 10 Enterprise"   →  "Windows 10 Enterprise"
SCCM            "Win10 Enterprise"        →  "Windows 10 Enterprise"
ServiceNow      "Microsoft Windows 10"    →  "Windows 10"
SCCM            "Win Server 2016 Std"     →  "Windows Server 2016 Standard"
VMware          "Microsoft Windows Server 2019" → "Windows Server 2019 Standard"
```

### Asset Type Normalization

```
Raw Value         Normalized
──────────────────────────────
"Workstation"  →  WORKSTATION
"Desktop"      →  WORKSTATION
"Laptop"       →  WORKSTATION
"End User Device" → WORKSTATION
"Server"       →  SERVER
"VM"           →  VIRTUAL_MACHINE
"Virtual Machine" → VIRTUAL_MACHINE
```

### Software Normalization

```
Raw Value                          Product    Version  Edition
────────────────────────────────────────────────────────────────
"MS Office Pro Plus 2019"       →  Office     2019     Professional Plus
"Microsoft Office Professional  →  Office     2019     Professional Plus
 Plus 2019"
"Office 2019 Professional Plus" →  Office     2019     Professional Plus
"SQL Server 2019 Standard Ed."  →  SQL Server 2019     Standard
"SQL Server 2019 Enterprise"    →  SQL Server 2019     Enterprise
"Visual Studio Enterprise 2019" →  Visual Studio 2019  Enterprise
"Exchange Server 2019"          →  Exchange Server 2019 Standard
```

### Deduplication Logic

```
Processing TAILWIND-WS-001 from SCCM:
  → Already exists from AD sync
  → Skip asset creation
  → Still save software deployments
  → Update hasScriptOutput = true

Processing TAILWIND-VM-001 from VMware:
  → Does not exist yet
  → Create new asset as VIRTUAL_MACHINE
  → hasScriptOutput = false (no software from VMware)
```

---

## Coverage Analysis and Extrapolation

### Coverage Calculation

```java
coveragePercentage = (coveredAssets / totalAssets) × 100
extrapolationFactor = totalAssets / coveredAssets
```

### Extrapolation Logic

**OS Products (DESKTOP_OS, SERVER_OS):**
```
Count from ALL assets — AD and VMware provide OS for everyone
No extrapolation needed
```

**Software Products (DATABASE, PRODUCTIVITY, DEVELOPER_TOOLS etc.):**
```
Step 1: Count deployments in covered machines only
Step 2: Calculate deployment rate
        rate = deployedInCovered / coveredAssets
Step 3: Estimate on uncovered machines
        extrapolated = ceil(rate × uncoveredAssets)
Step 4: Final total
        total = deployedInCovered + extrapolated
```

### Example with Tailwind Traders

```
Total: 12  |  Covered: 8  |  Uncovered: 4

Office Professional Plus:
  Found in covered: 5 machines
  Rate: 5/8 = 62.5%
  Extrapolated from 4 uncovered: ceil(0.625 × 4) = 3
  Final: 5 + 3 = 8 ← used for compliance calculation

SQL Server Enterprise:
  Found in covered: 2 machines
  Rate: 2/8 = 25%
  Extrapolated: ceil(0.25 × 4) = 1
  Final: 2 + 1 = 3
```

---

## Compliance Engine

The compliance engine runs per product per company:

```java
for each product in entitlements:

    if OS product:
        deployedQty = count from ALL assets by OS match
        extrapolatedQty = deployedQty (no extrapolation)

    if Software product:
        deployedInCovered = count from asset_deployments
                            (covered assets only)
        rate = deployedInCovered / coveredCount
        extrapolatedQty = deployedInCovered
                        + ceil(rate × uncoveredCount)

    apply licensing rules → additionalCoverage
    effectiveLicensed = licensedQty + additionalCoverage
    gap = effectiveLicensed - extrapolatedQty

    if gap == 0 → COMPLIANT
    if gap < 0  → UNDER_LICENSED
    if gap > 0  → OVER_LICENSED
```

---

## Licensing Rules Engine

Rules implement a common interface:

```java
public interface LicensingRule {
    String getRuleName();
    int applyRule(Product deployedProduct,
                  int deployedQty,
                  List<Entitlement> allEntitlements);
}
```

### Rule 1 — Version Downgrade Rights

A newer license covers an older deployment of the same edition.

```
Downgrade Matrix:
  Windows:        11 → 10 → 8.1 → 8 → 7
  Windows Server: 2022 → 2019 → 2016 → 2012
  SQL Server:     2022 → 2019 → 2017 → 2016 → 2014
  Office:         2021 → 2019 → 2016 → 2013
  Exchange:       2019 → 2016 → 2013
  Visio:          2021 → 2019 → 2016
  Project:        2021 → 2019 → 2016
  Visual Studio:  2022 → 2019 → 2017 → 2015

Edition downgrade is NOT allowed:
  Enterprise license does NOT cover Standard deployment
  Standard license does NOT cover Datacenter deployment
```

### Rule 2 — Software Assurance Upgrade Rights

An older license with SA flag covers a newer deployment.

```
SQL Server 2016 Standard + SA (hasSoftwareAssurance = true)
  → covers SQL Server 2019 Standard deployment
  → SA provides version upgrade rights
```

### Extensibility

New rules can be added by implementing `LicensingRule` and annotating with `@Component`. Spring auto-discovers and applies all rules. This follows the Open/Closed Principle.

---

## ELP Report

Generated as a downloadable Excel file using Apache POI.

### Tab 1 — Summary
```
Company name, report date, engagement status
Coverage summary: total assets, covered %, extrapolation factor
Compliance summary: products analyzed, compliant, under, over
```

### Tab 2 — Coverage Analysis
```
Total in-scope assets   Count   Percentage
Covered assets          8       66.67%
Uncovered assets        4       33.33%
Total workstations      5
Covered workstations    5
Total servers           3
Covered servers         3
```

### Tab 3 — Asset Register
```
Machine Name       OS                      Type          Domain         Covered  Last Seen
TAILWIND-WS-001   Windows 11 Enterprise   WORKSTATION   tailwind.local  Yes     2024-02-10
TAILWIND-VM-001   Windows Server 2022     VIRTUAL_MACHINE  -            No      -
```

### Tab 4 — Compliance Results
```
Product        Version  Edition            Category    Deployed  Licensed  Gap  Status
Office         2019     Professional Plus  PRODUCTIVITY    8         6      -2   UNDER_LICENSED
SQL Server     2019     Enterprise         DATABASE        3         1      -2   UNDER_LICENSED
Visio          2019     Standard           PRODUCTIVITY    2         2       0   COMPLIANT
```

### Tab 5 — Entitlements
```
Product        Version  Edition            Qty  License Type  Metric      SA
Office         2019     Professional Plus   6   EA            PER_DEVICE  Yes
SQL Server     2019     Enterprise          1   VOLUME        PER_CORE    No
```

---

## API Reference

### Authentication
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login, returns JWT token |

### Company
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/companies` | Create company |
| GET | `/api/companies` | List all companies |
| GET | `/api/companies/{id}` | Get company by ID |
| PUT | `/api/companies/{id}` | Update company |
| DELETE | `/api/companies/{id}` | Delete company |

### Products
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/products` | Get all 24 products |
| GET | `/api/products/search?name=SQL` | Search by name |
| GET | `/api/products/category/DATABASE` | Filter by category |

### Asset Ingestion (Legacy CSV)
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/assets/ingest/ad-export/{companyId}` | Upload AD Export CSV |
| GET | `/api/assets/company/{companyId}` | Get assets for company |

### Connectors
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/connectors/sync/{companyId}/{type}` | Sync single connector |
| POST | `/api/connectors/sync-all/{companyId}` | Sync all 4 connectors |

Connector types: `AD_EXPORT`, `SCCM_EXPORT`, `VMWARE_EXPORT`, `SERVICENOW_EXPORT`

### Coverage
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/coverage/company/{companyId}` | Get coverage analysis |

### Virtualization
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/virtualization/ingest/{companyId}` | Upload VMware CSV |
| GET | `/api/virtualization/topology/{companyId}` | Get VM topology |

### Entitlements
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/entitlements/company/{companyId}/ingest` | Upload MLS CSV |
| GET | `/api/entitlements/company/{companyId}` | Get entitlements |

### Compliance
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/compliance/run/{companyId}` | Run compliance check |
| GET | `/api/compliance/results/{companyId}` | Get results |

### Reports
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/reports/elp/{companyId}` | Download ELP Excel report |

### Audit
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/audit/recent` | Last 50 audit logs |
| GET | `/api/audit/company/{companyId}` | Logs by company |

---

## Database Schema

```
companies
  id, name, industry, contact_email, status, created_at

users
  id, full_name, email, password, role, enabled, created_at

products
  id, name, version, edition, category, is_active, created_at

assets
  id, machine_name, operating_system, asset_type,
  asset_source, domain, ip_address, last_seen,
  has_script_output, is_in_scope, company_id, created_at

asset_deployments
  id, asset_id, company_id, product_name, product_version,
  product_edition, raw_name, source, normalized, created_at

virtual_machines
  id, vm_name, host_name, cluster_name, num_cpu,
  memory_gb, guest_os, company_id, created_at

entitlements
  id, company_id, product_id, quantity, license_type,
  license_metric, has_software_assurance, source,
  purchase_date, expiry_date, po_number, notes, created_at

compliance_results
  id, company_id, product_id, deployed_quantity,
  extrapolated_quantity, licensed_quantity, gap,
  status, notes, calculated_at

audit_logs
  id, action, entity_type, entity_id, performed_by,
  company_id, details, ip_address, created_at

connectors
  id, company_id, connector_type, status, display_name,
  last_sync_at, last_sync_records, last_sync_status, created_at
```

---

## Running Locally

### Prerequisites
- Java 17+
- PostgreSQL 15
- Maven 3.9+

### Database Setup

```sql
CREATE DATABASE elp_compliance;
CREATE USER elp_user WITH PASSWORD 'elp_pass';
GRANT ALL PRIVILEGES ON DATABASE elp_compliance TO elp_user;
```

### Configuration

`src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/elp_compliance
spring.datasource.username=elp_user
spring.datasource.password=elp_pass
spring.jpa.hibernate.ddl-auto=update
jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
jwt.expiration=604800000
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```

### Run

```bash
mvn spring-boot:run
```

App starts on `http://localhost:8080`

Swagger UI: `http://localhost:8080/swagger-ui/index.html`

### Quick Test Flow

```bash
# 1. Register
POST /api/auth/register
{"fullName":"Admin","email":"admin@elp.com","password":"admin123","role":"ADMIN"}

# 2. Login → copy token
POST /api/auth/login
{"email":"admin@elp.com","password":"admin123"}

# 3. Create company
POST /api/companies
{"name":"Contoso Ltd","industry":"Technology","contactEmail":"admin@contoso.com","status":"ACTIVE"}

# 4. Sync all connectors (companyId = 1)
POST /api/connectors/sync-all/1

# 5. Check coverage
GET /api/coverage/company/1

# 6. Upload entitlements CSV
POST /api/entitlements/company/1/ingest (multipart file)

# 7. Run compliance
POST /api/compliance/run/1

# 8. Download ELP report
GET /api/reports/elp/1
```

---

## Sample Data

Two client companies included:

**Contoso Technologies Pvt Ltd (companyId = 1)**
- 21 assets (workstations, servers, VMs)
- 11 software deployments
- 7 entitlements
- Mix of OVER_LICENSED and UNDER_LICENSED products

**Tailwind Traders Pvt Ltd (companyId = 3)**
- 12 assets (5 workstations, 3 servers, 4 VMs)
- 13 software deployments
- 9 entitlements
- Key findings:
  - Office Pro Plus 2019: UNDER_LICENSED by 2
  - SQL Server 2019 Enterprise: UNDER_LICENSED by 2
  - Windows 11 Pro: OVER_LICENSED (wrong edition bought)

Sample CSV files in `/sample-data/`:
- `contoso-ad-export.csv`
- `contoso-vmware-export.csv`
- `contoso-entitlements.csv`
- `tailwind-entitlements.csv`

---

## Interview Talking Points

### On the Connector Architecture
"We built a pluggable connector architecture where each connector implements a common DataConnector interface. We have AD, SCCM, VMware and ServiceNow connectors. Each fetches raw data from its source, passes it through a normalization layer that converts different field names and value formats to our standard Asset model, and the orchestrator handles deduplication before saving. The Compliance Engine never knows where the data came from. If a new client uses Ivanti or Tanium, we only write a new connector — nothing else changes. This is the Open/Closed Principle."

### On Extrapolation
"We separate OS products from software products in the compliance engine. OS data comes from AD and VMware for all machines so no extrapolation is needed. Software data only comes from SCCM and scripts for covered machines. For each software product we calculate the deployment rate among covered machines — for example if Office is on 5 out of 8 covered machines, that's 62.5%. We then apply that rate to the 4 uncovered machines: ceil(0.625 × 4) = 3 estimated additional deployments, giving us a total extrapolated count of 8."

### On Licensing Rules
"We implemented a rule engine where each licensing rule implements a common interface. Currently we have Version Downgrade Rights — a newer license covers older deployments of the same edition — and Software Assurance Upgrade Rights — an older license with SA covers newer deployments. New rules can be added by implementing the interface and annotating with @Component. Spring auto-discovers them. We're aware of additional rules including CAL models, virtualization rights under Standard vs Datacenter editions, and core licensing for SQL Server."

### On the ELP Report
"The report is generated as a downloadable Excel file using Apache POI with 5 tabs — Summary, Coverage Analysis, Asset Register, Compliance Results, and Entitlements. It's returned as a byte array with the correct MIME type so Postman or any HTTP client can save it directly. The filename includes a timestamp for version tracking."

---

## Author

**Shobhit** — Microsoft License Contract Compliance, EY
GitHub: [shobhit1502](https://github.com/shobhit1502)
## Production Readiness Enhancements

Beyond the core 11 phases, the system has been hardened with production-grade infrastructure patterns: database query optimization, distributed caching, and asynchronous event-driven processing.

```
Phase 12 — Query Optimization + Indexes
Phase 13 — Redis Caching
Phase 14 — Kafka Async Processing
```

---

### Query Optimization + Indexes

**Problem:** With only primary key indexes, every company-scoped query (`WHERE company_id = ?`) performed a full table scan. The Compliance Engine also suffered from an N+1 query pattern — fetching each product individually instead of in bulk. Coverage calculation loaded entire asset lists into memory just to count them.

**What was changed:**

```
14 custom indexes added:
  assets:              company_id, has_script_output,
                       company_id+has_script_output (composite),
                       machine_name+company_id (composite)
  asset_deployments:   company_id, asset_id, product_name,
                       company_id+product_name (composite)
  compliance_results:  company_id, status
  entitlements:        company_id, product_id
  audit_logs:          company_id, created_at
```

**N+1 fix in ComplianceEngine:**

```java
// Before — N queries, one per product
for (Long productId : licensedByProduct.keySet()) {
    Product product = productRepository.findById(productId)
            .orElse(null);
}

// After — 1 query for all products
List<Long> productIds = new ArrayList<>(licensedByProduct.keySet());
Map<Long, Product> productMap = productRepository
        .findAllById(productIds)
        .stream()
        .collect(Collectors.toMap(Product::getId, p -> p));
```

**Count queries instead of loading full lists:**

```java
// Before — loads ALL assets to count in Java
List<Asset> assets = assetRepository.findByCompanyId(companyId);
long coveredCount = assets.stream()
        .filter(Asset::isHasScriptOutput).count();

// After — database does the counting
long coveredCount = assetRepository.countCoveredByCompanyId(companyId);
```

New repository methods added: `countByCompanyId`, `countCoveredByCompanyId`, `countWorkstationsByCompanyId`, `countCoveredWorkstationsByCompanyId`, `countServersByCompanyId`, `countCoveredServersByCompanyId`, `findCoveredByCompanyId`.

**Unique constraint added** to prevent duplicate machine records during concurrent connector syncs:

```java
@Table(name = "assets",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_machine_company",
            columnNames = {"machine_name", "company_id"})
    })
```

**Verification:** At the current dataset size (33 assets), PostgreSQL's query planner correctly chooses sequential scan over index scan — this is expected optimizer behavior for small tables, not a failure of indexing. Verified with `EXPLAIN ANALYZE` and by forcing index usage via `SET enable_seqscan = OFF`. At production scale (10,000+ assets) the same indexes will be selected automatically.

---

### Redis Caching

**Problem:** The coverage analysis endpoint executed 6 COUNT queries against PostgreSQL on every request, even when called repeatedly with no underlying data change.

**What was added:**

```
Dependency:    spring-boot-starter-data-redis
Cache config:  com.elp.compliance_manager.config.RedisConfig
Cached method: CoverageService.calculateCoverage()
Eviction:      CoverageService.evictCoverageCache()
              called from ConnectorOrchestrator after every sync
```

**Cache configuration:**

```
Cache Name         Key              TTL       Eviction Trigger
─────────────────────────────────────────────────────────────
coverage            company:{id}    1 hour    After connector sync
complianceResults   company:{id}    30 min    (planned)
products            catalog         24 hours  Never (rarely changes)
```

**Behavior:**

```java
@Cacheable(value = "coverage", key = "#companyId")
public CoverageResponseDTO calculateCoverage(Long companyId) {
    // Only executes on cache miss — 6 DB queries
}

@CacheEvict(value = "coverage", key = "#companyId")
public void evictCoverageCache(Long companyId) {
    // Called after connector sync — forces fresh data next read
}
```

**Verified results:**

```
Cache MISS (first call):  6 Hibernate COUNT queries, ~50ms
Cache HIT (second call):  0 Hibernate queries, served from Redis, ~2ms
Cache EVICT (after sync): Redis key deleted automatically,
                          next request is a fresh MISS
```

All DTOs cached in Redis implement `Serializable` and include `@NoArgsConstructor` / `@AllArgsConstructor` to support Jackson-based deserialization via `GenericJackson2JsonRedisSerializer`.

---

### Kafka Async Processing

**Problem:** `POST /api/connectors/sync-all/{companyId}` blocked the HTTP request thread for the full duration of all 4 connector syncs. At enterprise scale (thousands of assets) this would cause client-side timeouts.

**What was added:**

```
Dependency:  spring-kafka
Mode:        KRaft (Kafka 4.x — no Zookeeper dependency)
Topics:      connector-sync-requests   (3 partitions)
            connector-sync-completed  (3 partitions)
            compliance-run-requests   (3 partitions)
            audit-events              (1 partition)
```

**New package:** `com.elp.compliance_manager.job`

```
SyncJob.java            — entity tracking job lifecycle
JobStatus.java          — enum: QUEUED, IN_PROGRESS, COMPLETED, FAILED
SyncJobRepository.java  — JPA repository for sync_jobs table
SyncRequestEvent.java   — Kafka message payload (Serializable)
SyncEventProducer.java  — publishes sync requests, creates SyncJob record
SyncEventConsumer.java  — @KafkaListener, runs actual sync, updates status
SyncJobController.java  — exposes job request + polling endpoints
```

**Async flow:**

```
POST /api/jobs/sync/{companyId}?connectorType=ALL
        ↓
SyncEventProducer publishes event to Kafka, creates SyncJob (QUEUED)
        ↓
Returns immediately: 202 Accepted { jobId, status: "QUEUED" }
        ↓
SyncEventConsumer (background thread) receives event from Kafka
        ↓
Status updated → IN_PROGRESS
        ↓
ConnectorOrchestrator.syncAll() runs (same logic as synchronous sync)
        ↓
Status updated → COMPLETED (or FAILED with errorMessage)
        ↓
Coverage Redis cache evicted as part of normal sync flow
        ↓
Client polls: GET /api/jobs/{jobId} → final result
```

**New endpoints:**

```
POST /api/jobs/sync/{companyId}?connectorType=ALL   — queue async sync
GET  /api/jobs/{jobId}                              — poll job status
GET  /api/jobs/company/{companyId}                  — job history
```

**Verified end-to-end:** A real test run completed in 474ms — `POST /api/jobs/sync/3` returned `202 Accepted` with a jobId instantly; polling `GET /api/jobs/{jobId}` showed `COMPLETED` with `assetsProcessed: 20`. Console logs confirmed the full trace: producer publish → consumer receive → status IN_PROGRESS → ConnectorOrchestrator ran all 4 connectors → deduplication skipped existing assets → Redis cache evicted → status COMPLETED → job saved to PostgreSQL.

This demonstrates the three production features working together in a single flow: Kafka triggers the sync, the sync evicts the Redis cache, and the optimized count queries serve the next coverage request.

---

### Updated Database Schema

```
sync_jobs
  job_id (PK, UUID), company_id, connector_type, status,
  assets_processed, assets_saved, deployments_saved,
  error_message, requested_at, completed_at
```

---

### Production Readiness — Interview Talking Points

**On Query Optimization:**
"I added 14 indexes on frequently filtered columns and fixed an N+1 query in the Compliance Engine by batch-loading products with `findAllById` instead of looping `findById`. I also replaced full asset list loads with COUNT queries at the database level. PostgreSQL's planner currently chooses sequential scan over index scan because our dataset is small — that's correct optimizer behavior, not a sign the indexes aren't working. I verified this with `EXPLAIN ANALYZE` and by explicitly disabling sequential scan to confirm the index scan plan exists and is valid."

**On Redis:**
"The coverage endpoint was hitting PostgreSQL with 6 COUNT queries on every call. I cached the result in Redis with a 1-hour TTL using Spring's `@Cacheable`, and explicitly evict that cache with `@CacheEvict` whenever a connector sync changes the underlying asset data. This took the response time from about 50ms to about 2ms on cache hits, while guaranteeing the client never sees stale data after a sync."

**On Kafka:**
"Connector sync was a synchronous, blocking call — fine for our test data but a problem at real enterprise scale with thousands of assets. I moved it to an async event-driven model using Kafka in KRaft mode. The API now returns a 202 with a jobId immediately, a background consumer does the actual work, and job state — QUEUED, IN_PROGRESS, COMPLETED, FAILED — is persisted in PostgreSQL so the client can poll for status. I tested the full round trip and confirmed the producer, consumer, deduplication logic, and Redis cache eviction all fire correctly within the async flow."

**On the integration story:**
"These three features aren't isolated — they compose. When the Kafka consumer finishes a sync in the background, it calls the same `ConnectorOrchestrator` used by the synchronous path, which evicts the Redis coverage cache. So whether a sync happens via the blocking endpoint or the async Kafka-backed one, the next coverage read is guaranteed fresh, and that read itself benefits from the optimized count queries underneath."

---

### What's Next (Planned, Not Yet Implemented)

```
MongoDB raw connector storage — persist raw pre-normalization
  responses per sync for audit/reprocessing capability
Centralized logging (Graylog / Last9) — structured log shipping
  across instances for production observability
Refresh token endpoint — shorter-lived access tokens in production
SXSSFWorkbook — streaming Excel generation for large ELP reports
```
