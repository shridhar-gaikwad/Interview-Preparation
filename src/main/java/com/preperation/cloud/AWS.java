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
