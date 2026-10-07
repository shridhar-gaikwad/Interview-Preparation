package com.preperation.cloud;

/*
 * =====================================================================================
 *                      AWS (AMAZON WEB SERVICES) - FUNDAMENTALS
 * =====================================================================================
 *
 * WHAT IS IT?
 * -----------
 * AWS = a CLOUD PLATFORM that rents computing resources (servers, storage, databases,
 * networking) ON-DEMAND over the internet, so you PAY-AS-YOU-GO instead of buying and
 * maintaining your own hardware.
 *
 * >>> NOTE ON THIS FILE <<<
 * This is a HIGH-LEVEL INDEX of AWS fundamentals + the most frequently asked interview
 * points. It stays practical - enough to speak confidently, not an exhaustive manual.
 *
 * -------------------------------------------------------------------------------------
 * WHY CLOUD? (on-premise vs cloud)
 * -------------------------------------------------------------------------------------
 *   On-Premise                     | Cloud (AWS)
 *   ------------------------------ | ---------------------------------
 *   Buy & maintain hardware        | Rent on-demand, no upfront hardware
 *   Fixed capacity                 | Elastic (scale up/down anytime)
 *   High upfront cost (CapEx)      | Pay-as-you-go (OpEx)
 *   You manage everything          | AWS manages the infrastructure
 *
 * -------------------------------------------------------------------------------------
 * CLOUD SERVICE MODELS (know the difference)
 * -------------------------------------------------------------------------------------
 *   IaaS (Infrastructure) -> you get raw VMs/network; you manage OS & app.  e.g. EC2
 *   PaaS (Platform)       -> you deploy code; AWS manages runtime/servers.  e.g. Elastic Beanstalk
 *   SaaS (Software)       -> ready-to-use software over the web.           e.g. Gmail
 *   (Serverless / FaaS)   -> run functions, no servers to manage.          e.g. Lambda
 *
 * DEPLOYMENT MODELS: Public cloud | Private cloud | Hybrid (mix of on-prem + cloud).
 *
 * -------------------------------------------------------------------------------------
 * GLOBAL INFRASTRUCTURE (very common question)
 * -------------------------------------------------------------------------------------
 *   - Region              -> a geographic area (e.g. ap-south-1 Mumbai).
 *   - Availability Zone   -> one or more isolated data centers within a region.
 *                            Deploy across MULTIPLE AZs for HIGH AVAILABILITY.
 *   - Edge Location       -> CDN cache points (CloudFront) close to users.
 *
 * -------------------------------------------------------------------------------------
 * CORE SERVICES (grouped by concern)
 * -------------------------------------------------------------------------------------
 *   COMPUTE
 *     - EC2            -> virtual servers (resizable VMs).
 *     - Lambda         -> serverless functions (run code, no servers, pay per invocation).
 *     - ECS / EKS      -> run Docker containers / Kubernetes.
 *     - Elastic Beanstalk -> PaaS: just upload code, it handles the rest.
 *
 *   STORAGE
 *     - S3             -> object storage (files, images, backups); 11 9's durability.
 *     - EBS            -> block storage (a disk attached to ONE EC2 instance).
 *     - EFS            -> shared file storage (mountable by MANY instances).
 *     - Glacier        -> cheap, cold archival storage.
 *
 *   DATABASE
 *     - RDS            -> managed relational DB (MySQL, Postgres, Oracle, SQL Server).
 *     - Aurora         -> AWS's high-performance MySQL/Postgres-compatible DB.
 *     - DynamoDB       -> managed NoSQL (key-value), single-digit ms latency.
 *     - ElastiCache    -> managed Redis / Memcached (caching).
 *
 *   NETWORKING
 *     - VPC            -> your private, isolated network in AWS.
 *     - Route 53       -> DNS + domain routing.
 *     - CloudFront     -> CDN (caches content at edge locations).
 *     - API Gateway    -> managed entry point for APIs (often + Lambda).
 *     - ELB            -> Elastic Load Balancer (distributes traffic).
 *
 *   SECURITY & IDENTITY
 *     - IAM            -> users, groups, roles, policies (WHO can do WHAT).
 *     - KMS            -> encryption key management.
 *     - Cognito        -> user sign-up / sign-in / auth for apps.
 *
 *   MESSAGING / INTEGRATION
 *     - SQS            -> message QUEUE (async, decoupling, point-to-point).
 *     - SNS            -> pub/sub NOTIFICATIONS (fan-out to many subscribers).
 *     - Kinesis        -> real-time streaming data.
 *
 *   MONITORING / MANAGEMENT
 *     - CloudWatch     -> metrics, logs, alarms, dashboards.
 *     - CloudTrail     -> audit log of every API call (who did what).
 *     - Auto Scaling   -> add/remove EC2 instances based on load.
 *
 * -------------------------------------------------------------------------------------
 * IAM ESSENTIALS (frequently asked)
 * -------------------------------------------------------------------------------------
 *   - User    -> a person/app with credentials.
 *   - Group   -> a set of users sharing permissions.
 *   - Role    -> temporary permissions assumed by a service/user (no long-term keys).
 *   - Policy  -> JSON document that GRANTS/DENIES actions on resources.
 *   BEST PRACTICE: least privilege, enable MFA, prefer ROLES over access keys.
 *
 * -------------------------------------------------------------------------------------
 * SHARED RESPONSIBILITY MODEL (almost always asked)
 * -------------------------------------------------------------------------------------
 *   AWS is responsible FOR the cloud: physical hardware, data centers, network
 *   infrastructure, hypervisor, and the managed service internals.
 *   YOU are responsible IN the cloud: data, IAM config, OS patching (on EC2),
 *   firewall/Security Group rules, encryption choices, and application-level security.
 *   -> The split shifts with the service model: on EC2 you patch the OS; on RDS/Lambda
 *      AWS patches the OS/runtime and you're only responsible for data & access config.
 *
 * -------------------------------------------------------------------------------------
 * SECURITY GROUP vs NACL (common follow-up to Q8)
 * -------------------------------------------------------------------------------------
 *   Security Group (SG)                     | Network ACL (NACL)
 *   ---------------------------------------- | ----------------------------------------
 *   Operates at INSTANCE level               | Operates at SUBNET level
 *   STATEFUL (return traffic auto-allowed)   | STATELESS (must allow both directions)
 *   Only ALLOW rules                         | Supports ALLOW and DENY rules
 *   Evaluates ALL rules before deciding       | Rules processed IN ORDER (lowest number first)
 *
 * -------------------------------------------------------------------------------------
 * EC2 PRICING MODELS (cost-focused question)
 * -------------------------------------------------------------------------------------
 *   - On-Demand   -> pay per second/hour, no commitment; good for unpredictable workloads.
 *   - Reserved    -> 1-3 year commitment for big discount; steady, predictable workloads.
 *   - Spot        -> bid on spare capacity, up to ~90% cheaper, can be reclaimed anytime;
 *                    good for fault-tolerant/batch jobs.
 *   - Savings Plans -> flexible $/hour commitment across instance families, like Reserved
 *                    but less rigid.
 *   - Dedicated Host/Instance -> physical server dedicated to you (licensing/compliance).
 *
 * -------------------------------------------------------------------------------------
 * S3 STORAGE CLASSES & CONSISTENCY (very common)
 * -------------------------------------------------------------------------------------
 *   - Standard            -> frequently accessed data, millisecond access.
 *   - Intelligent-Tiering -> auto-moves objects between tiers based on access pattern.
 *   - Standard-IA / One Zone-IA -> infrequent access, cheaper storage, retrieval fee.
 *   - Glacier / Glacier Deep Archive -> archival, retrieval takes minutes to hours.
 *   CONSISTENCY: S3 provides STRONG READ-AFTER-WRITE consistency for all operations
 *   (PUTs and DELETEs) since Dec 2020 - no more "eventual consistency" caveat to worry about.
 *
 * -------------------------------------------------------------------------------------
 * VPC COMPONENTS (networking deep-dive)
 * -------------------------------------------------------------------------------------
 *   - Subnet            -> a slice of a VPC's IP range tied to one AZ.
 *   - Public subnet     -> has a route to an Internet Gateway (IGW).
 *   - Private subnet    -> no direct route to internet; outbound-only via NAT Gateway.
 *   - Internet Gateway  -> allows a VPC to talk to the public internet.
 *   - NAT Gateway        -> lets PRIVATE subnet resources reach the internet (outbound
 *                          only) without being reachable from outside.
 *   - Route Table        -> decides where network traffic from a subnet is directed.
 *   TYPICAL PATTERN: web/app tier in public subnet behind ELB, DB tier in private subnet.
 *
 * -------------------------------------------------------------------------------------
 * RDS HIGH AVAILABILITY: Multi-AZ vs Read Replica (frequently confused)
 * -------------------------------------------------------------------------------------
 *   Multi-AZ                                  | Read Replica
 *   ------------------------------------------ | ------------------------------------------
 *   For DISASTER RECOVERY / failover           | For READ SCALING (offload read traffic)
 *   Synchronous replication to standby          | Asynchronous replication
 *   Standby NOT readable; auto-failover on fail | Replica IS readable; promote manually
 *   Same region only                            | Can be cross-region
 *
 * -------------------------------------------------------------------------------------
 * AWS WELL-ARCHITECTED FRAMEWORK (design-principles question)
 * -------------------------------------------------------------------------------------
 *   6 pillars: Operational Excellence, Security, Reliability, Performance Efficiency,
 *   Cost Optimization, Sustainability.
 *   -> Interviewers often ask "how would you design a highly available/cost-efficient
 *      system?" - structure your answer around these pillars.
 *
 * -------------------------------------------------------------------------------------
 * SCALABILITY & HIGH AVAILABILITY
 * -------------------------------------------------------------------------------------
 *   - Vertical scaling   -> bigger EC2 instance.
 *   - Horizontal scaling -> more EC2 instances behind an ELB (Auto Scaling Group).
 *   - Multi-AZ           -> run across zones so one AZ failure doesn't take you down.
 *   - S3 + CloudFront    -> scalable static content delivery.
 *
 * -------------------------------------------------------------------------------------
 * QUICK Q&A (INTERVIEW)
 * -------------------------------------------------------------------------------------
 * Q1: What is cloud computing? -> On-demand delivery of IT resources over the internet
 *     with pay-as-you-go pricing.
 * Q2: IaaS vs PaaS vs SaaS? -> Raw infra (EC2) vs managed platform (Beanstalk) vs ready
 *     software (Gmail).
 * Q3: Region vs Availability Zone? -> A region is a geographic area; an AZ is an isolated
 *     data center within it. Use multiple AZs for HA.
 * Q4: What is EC2? -> Resizable virtual servers in the cloud.
 * Q5: What is S3? -> Highly durable object storage for files/backups (not a filesystem).
 * Q6: EBS vs S3? -> EBS = block disk for ONE EC2; S3 = object store accessed via API.
 * Q7: What is IAM? -> Service to manage users/roles/policies - who can access what.
 * Q8: What is a Security Group? -> A virtual firewall controlling inbound/outbound
 *     traffic to an instance (stateful).
 * Q9: SQS vs SNS? -> SQS = queue (one consumer pulls); SNS = pub/sub (push to many).
 * Q10: What is Lambda? -> Serverless compute - run code on events, pay per execution.
 * Q11: RDS vs DynamoDB? -> RDS = managed SQL; DynamoDB = managed NoSQL (key-value).
 * Q12: How do you make an app highly available? -> Multi-AZ, Auto Scaling, ELB, and
 *     managed services with built-in redundancy.
 * Q13: What is a VPC? -> Your own isolated virtual network within AWS.
 * Q14: How to reduce AWS cost? -> Right-size instances, Reserved/Spot instances,
 *     auto scaling, S3 lifecycle policies, and delete idle resources.
 * Q15: What is the Shared Responsibility Model? -> AWS secures the cloud (hardware,
 *     network, hypervisor); you secure what's IN the cloud (data, IAM, OS patches,
 *     SG rules) - the exact split depends on whether it's IaaS, PaaS, or serverless.
 * Q16: Security Group vs NACL? -> SG is stateful & instance-level (allow-only); NACL is
 *     stateless & subnet-level (allow + deny, rule order matters).
 * Q17: Multi-AZ vs Read Replica in RDS? -> Multi-AZ = synchronous standby for failover/DR
 *     (not readable); Read Replica = asynchronous copy for scaling reads (readable, can
 *     be promoted to a standalone DB).
 * Q18: On-Demand vs Reserved vs Spot instances? -> On-Demand = no commitment, pay as you
 *     go; Reserved = 1-3yr commitment for discount; Spot = spare capacity at steep
 *     discount but can be reclaimed - use for fault-tolerant/batch workloads.
 * Q19: What is a NAT Gateway used for? -> Lets instances in a PRIVATE subnet initiate
 *     outbound internet traffic (e.g. for updates) without being directly reachable
 *     from the internet.
 * Q20: What happens when an EC2 instance in an Auto Scaling Group fails a health check?
 *     -> ASG terminates the unhealthy instance and launches a replacement automatically
 *     to maintain the desired capacity.
 * Q21: How would you design a highly available, fault-tolerant 3-tier web app on AWS?
 *     -> Route 53 -> CloudFront/ALB -> EC2 Auto Scaling Group across multiple AZs (public
 *     subnets) -> RDS Multi-AZ (private subnet) + ElastiCache for caching + S3 for static
 *     assets; secure with SGs/NACLs and IAM roles (no hardcoded keys).
 * Q22: What is the difference between stopping and terminating an EC2 instance? ->
 *     Stop preserves the instance (and EBS root volume) for restart later, billing for
 *     compute pauses; Terminate permanently deletes the instance (and, by default, any
 *     EBS volumes marked "delete on termination").
 *
 * ONE-LINER SUMMARY:
 * AWS provides on-demand, pay-as-you-go compute (EC2/Lambda), storage (S3), databases
 * (RDS/DynamoDB), networking (VPC) and security (IAM) - letting you build scalable,
 * highly available systems without owning hardware.
 *
 * -------------------------------------------------------------------------------------
 * The main() below just prints a quick "service by concern" cheat sheet for revision.
 * =====================================================================================
 */
public class AWS {

    public static void main(String[] args) {
        String[][] cheatSheet = {
                {"Compute",     "EC2, Lambda, ECS/EKS, Elastic Beanstalk"},
                {"Storage",     "S3, EBS, EFS, Glacier"},
                {"Database",    "RDS, Aurora, DynamoDB, ElastiCache"},
                {"Networking",  "VPC, Route 53, CloudFront, API Gateway, ELB"},
                {"Security",    "IAM, KMS, Cognito"},
                {"Messaging",   "SQS, SNS, Kinesis"},
                {"Monitoring",  "CloudWatch, CloudTrail, Auto Scaling"}
        };
        System.out.println("== AWS Core Services (by concern) ==");
        for (String[] row : cheatSheet) {
            System.out.printf("  %-12s -> %s%n", row[0], row[1]);
        }
    }
}
