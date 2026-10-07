# Drools 10 KJAR 部署與載入

## 1. 使用情境

- Drools 規則是一個獨立的 KJAR 專案。
- Spring Boot Application 是另一個獨立專案。
- KJAR 透過 Maven 發布到 Nexus。
- Spring Boot Application 執行時依 Maven GAV 載入 KJAR。

```text
KJAR Project
    │ mvn deploy
    ▼
Nexus Maven Repository
    │ kie-ci
    ▼
Spring Boot Application
    │ newKieContainer(releaseId)
    ▼
KieContainer → KieSession
```

本文只說明下列 Drools 10 官方機制：

- KJAR
- `kmodule.xml`
- `kie-maven-plugin`
- `ReleaseId`
- `newKieContainer(releaseId)`
- `kie-ci`
- Maven repository/settings
- `KieScanner`

---

## 2. 建立 KJAR

### 2.1 目錄結構

```text
discount-rules/
├─ pom.xml
└─ src/main/resources/
   ├─ META-INF/
   │  └─ kmodule.xml
   └─ rules/
      ├─ nightclubDiscount/discount.drl
      ├─ eNightclubDiscount/discount.drl.xlsx
      └─ loan/loanRule.drl
```

KJAR 必須包含：

```text
src/main/resources/META-INF/kmodule.xml
```

### 2.2 KJAR Maven 設定

KJAR 的 `pom.xml` 需設定 `kjar` packaging：

```xml
<groupId>com.example.rules</groupId>
<artifactId>discount-rules</artifactId>
<version>1.0.0</version>
<packaging>kjar</packaging>

<properties>
    <drools.version>10.0.0</drools.version>
</properties>
```

加入 Drools dependencies：

```xml
<dependencies>
    <dependency>
        <groupId>org.drools</groupId>
        <artifactId>drools-engine</artifactId>
        <version>${drools.version}</version>
        <scope>provided</scope>
    </dependency>

    <!-- 使用 kmodule.xml 時需要 -->
    <dependency>
        <groupId>org.drools</groupId>
        <artifactId>drools-xml-support</artifactId>
        <version>${drools.version}</version>
        <scope>provided</scope>
    </dependency>

    <!-- 使用 XLS/XLSX Decision Table 時需要 -->
    <dependency>
        <groupId>org.drools</groupId>
        <artifactId>drools-decisiontables</artifactId>
        <version>${drools.version}</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

加入 `kie-maven-plugin`：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.kie</groupId>
            <artifactId>kie-maven-plugin</artifactId>
            <version>${drools.version}</version>
            <extensions>true</extensions>
        </plugin>
    </plugins>
</build>
```

`kie-maven-plugin` 會在 Maven build 階段處理並驗證 KJAR 規則。

### 2.3 `kmodule.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<kmodule xmlns="http://www.drools.org/xsd/kmodule">

    <kbase name="nightclub_discount_rules"
           packages="rules.nightclubDiscount">
        <ksession name="discountSession"/>
    </kbase>

    <kbase name="excel_nightclub_discount_rules"
           packages="rules.eNightclubDiscount">
        <ksession name="xlsDiscountSession"/>
    </kbase>

    <kbase name="loan_rules"
           packages="rules.loan">
        <ksession name="loanRulesSession"/>
    </kbase>

</kmodule>
```

`kmodule.xml` 定義 KieBase、規則 package 與 KieSession 名稱。

### 2.4 發布 KJAR

在 KJAR `pom.xml` 指定 Nexus：

```xml
<distributionManagement>
    <repository>
        <id>company-nexus</id>
        <url>https://nexus.example.com/repository/maven-releases/</url>
    </repository>

    <snapshotRepository>
        <id>company-nexus</id>
        <url>https://nexus.example.com/repository/maven-snapshots/</url>
    </snapshotRepository>
</distributionManagement>
```

建置並發布：

```bash
mvn clean deploy
```

發布後的 KJAR GAV 例如：

```text
com.example.rules:discount-rules:1.0.0
```

---

## 3. Maven Repository 設定

KJAR 發布及 Spring Boot 執行時下載 KJAR，都使用 Maven `settings.xml`。

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0">
    <servers>
        <server>
            <id>company-nexus</id>
            <username>${env.NEXUS_USERNAME}</username>
            <password>${env.NEXUS_PASSWORD}</password>
        </server>
    </servers>

    <profiles>
        <profile>
            <id>company-repositories</id>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>

            <repositories>
                <repository>
                    <id>company-nexus</id>
                    <url>https://nexus.example.com/repository/maven-public/</url>
                    <releases>
                        <enabled>true</enabled>
                    </releases>
                    <snapshots>
                        <enabled>true</enabled>
                        <updatePolicy>always</updatePolicy>
                    </snapshots>
                </repository>
            </repositories>
        </profile>
    </profiles>
</settings>
```

Drools 可以從以下位置讀取 Maven settings：

- Maven 安裝目錄的 `conf/settings.xml`
- `${user.home}/.m2/settings.xml`
- `kie.maven.settings.custom` 指定的位置

指定自訂設定檔：

```bash
java \
  -Dkie.maven.settings.custom=/app/config/settings.xml \
  -jar spring-application.jar
```

---

## 4. Spring Boot 載入 KJAR

### 4.1 加入 `kie-ci`

Spring Boot Application 需要 Drools runtime 與 `kie-ci`：

```xml
<dependency>
    <groupId>org.drools</groupId>
    <artifactId>drools-engine</artifactId>
</dependency>

<dependency>
    <groupId>org.drools</groupId>
    <artifactId>drools-xml-support</artifactId>
</dependency>

<dependency>
    <groupId>org.drools</groupId>
    <artifactId>drools-decisiontables</artifactId>
</dependency>

<dependency>
    <groupId>org.kie</groupId>
    <artifactId>kie-ci</artifactId>
</dependency>
```

上述版本可由 `drools-bom:10.0.0` 統一管理。

### 4.2 設定 KJAR GAV

`application.properties`：

```properties
drools.kjar.group-id=com.example.rules
drools.kjar.artifact-id=discount-rules
drools.kjar.version=1.0.0
```

### 4.3 建立 `ReleaseId` 與 `KieContainer`

```java
package com.example.hclai.drools_play_ground.config;

import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DroolsConfig {

    @Bean
    public KieContainer kieContainer(
            @Value("${drools.kjar.group-id}") String groupId,
            @Value("${drools.kjar.artifact-id}") String artifactId,
            @Value("${drools.kjar.version}") String version) {

        KieServices kieServices = KieServices.Factory.get();

        ReleaseId releaseId = kieServices.newReleaseId(
                groupId,
                artifactId,
                version
        );

        return kieServices.newKieContainer(releaseId);
    }
}
```

執行時：

1. `ReleaseId` 表示 KJAR 的 `groupId:artifactId:version`。
2. `newKieContainer(releaseId)` 要求載入該 KJAR。
3. `kie-ci` 使用 Maven settings 從 Nexus 解析 KJAR。

這裡不使用 `getKieClasspathContainer()`，因為 KJAR 不在 Spring Application classpath，而是從 Maven repository 動態載入。

### 4.4 建立 KieSession

Service 的使用方式不變：

```java
KieSession kieSession =
        kieContainer.newKieSession("discountSession");

try {
    kieSession.insert(data);
    kieSession.insert(result);
    kieSession.fireAllRules();
} finally {
    kieSession.dispose();
}
```

Session name 必須與 KJAR `kmodule.xml` 中的 `<ksession>` 名稱一致。

---

## 5. `KieScanner`

`KieScanner` 可以定期檢查 Maven repository 中同一個 SNAPSHOT KJAR 是否有更新。

```java
@Bean(destroyMethod = "shutdown")
public KieScanner kieScanner(KieContainer kieContainer) {
    KieScanner scanner = KieServices.Factory.get()
            .newKieScanner(kieContainer);

    scanner.start(10_000L);
    return scanner;
}
```

也可以手動檢查：

```java
scanner.scanNow();
```

版本行為：

- `1.0.0-SNAPSHOT` 可以取得同一 SNAPSHOT 的新建置。
- 固定版本 `1.0.0` 不會自動切換成其他版本。
- 不需要執行期間更新時，不需要建立 `KieScanner`。

Drools 10 官方文件將 KieScanner + SNAPSHOT 定位為開發環境用途，並提醒不要在正式環境使用 SNAPSHOT 自動更新。

---

## 6. 官方資料

- [Drools 10－Build, Deploy, Utilize and Run](https://kie.apache.org/docs/10.0.x/drools/drools/KIE/index.html)
- [Drools 10.0.0 Release Notes](https://kie.apache.org/docs/10.0.x/drools/drools/release-notes/index.html)
- [Apache KIE 10.0.0 artifacts](https://kie.apache.org/downloads/download_10_0_0/)
- [KieServices 10.0.0 API](https://javadoc.io/doc/org.kie/kie-api/10.0.0/org/kie/api/KieServices.html)
