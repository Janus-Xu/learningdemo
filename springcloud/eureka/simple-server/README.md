### 一、spring cloud 简介

spring cloud 为开发人员提供了快速构建分布式系统的一些工具，包括配置管理、服务发现、断路器、路由、微代理、事件总线、全局锁、决策竞选、分布式会话等等。它运行环境简单，可以在开发人员的电脑上跑。另外说明 spring cloud 是基于 springboot 的，所以需要开发中对 springboot 有一定的了解，如果不了解的话可以看这篇文章：[2 小时学会 springboot](http://blog.csdn.net/forezp/article/details/61472783)。另外对于 “微服务架构” 不了解的话，可以通过搜索引擎搜索“微服务架构” 了解下。

### 二、创建服务注册中心

在这里，我们需要用的的组件上 Spring Cloud Netflix 的 Eureka ,eureka 是一个服务注册和发现模块。

**2.1 首先创建一个 maven 主工程。**

**2.2 然后创建 2 个 model 工程:** 一个 model 工程作为服务注册中心，即 Eureka Server, 另一个作为 Eureka Client。

下面以创建 server 为例子，详细说明创建过程：

右键工程 -> 创建 model-> 选择 spring initialir 如下图：

![](http://upload-images.jianshu.io/upload_images/2279594-de33b84a79858106.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/600)

下一步 -> 选择 cloud discovery->eureka server , 然后一直下一步就行了。

![](http://upload-images.jianshu.io/upload_images/2279594-3addb73d569a58e6.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/600)

创建完后的工程的 pom.xml 文件如下：

```

<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.forezp</groupId>
    <artifactId>eurekaserver</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>eurekaserver</name>
    <description>Demo project for Spring Boot</description>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>1.5.2.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <!--eureka server -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-eureka-server</artifactId>
        </dependency>

        <!-- spring boot test-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>Dalston.RC1</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

    <repositories>
        <repository>
            <id>spring-milestones</id>
            <name>Spring Milestones</name>
            <url>https://repo.spring.io/milestone</url>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
        </repository>
    </repositories>

</project>

```

**2.3 启动一个服务注册中心**，只需要一个注解 @EnableEurekaServer，这个注解需要在 springboot 工程的启动 application 类上加：

```

@EnableEurekaServer
@SpringBootApplication
public class EurekaserverApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaserverApplication.class, args);
    }
}

```

**2.4 **eureka 是一个高可用的组件，它没有后端缓存，每一个实例注册之后需要向注册中心发送心跳（因此可以在内存中完成），在默认情况下 erureka server 也是一个 eureka client , 必须要指定一个 server。eureka server 的配置文件 appication.yml：

```
server:
  port: 8761

eureka:
  instance:
    hostname: localhost
  client:
    registerWithEureka: false
    fetchRegistry: false
    serviceUrl:
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/

```

通过 eureka.client.registerWithEureka：false 和 fetchRegistry：false 来表明自己是一个 eureka server.

**2.5** eureka server 是有界面的，启动工程, 打开浏览器访问：
[http://localhost:8761](http://localhost:8761) , 界面如下：

![](http://upload-images.jianshu.io/upload_images/2279594-8c954deeb3a3a01c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/600)

> No application available 没有服务被发现 ……^_^
> 因为没有注册服务当然不可能有服务被发现了。

### 三、创建一个服务提供者 (eureka client)

当 client 向 server 注册时，它会提供一些元数据，例如主机和端口，URL，主页等。Eureka server 从每个 client 实例接收心跳消息。 如果心跳超时，则通常将该实例从注册 server 中删除。

创建过程同 server 类似, 创建完 pom.xml 如下：

```

<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.forezp</groupId>
    <artifactId>service-hi</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>service-hi</name>
    <description>Demo project for Spring Boot</description>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>1.5.2.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-eureka</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>Dalston.RC1</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

    <repositories>
        <repository>
            <id>spring-milestones</id>
            <name>Spring Milestones</name>
            <url>https://repo.spring.io/milestone</url>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
        </repository>
    </repositories>

</project>

```

通过注解 @EnableEurekaClient 表明自己是一个 eurekaclient.

```
@SpringBootApplication
@EnableEurekaClient
@RestController
public class ServiceHiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceHiApplication.class, args);
    }

    @Value("${server.port}")
    String port;
    @RequestMapping("/hi")
    public String home(@RequestParam String name) {
        return "hi "+name+",i am from port:" +port;
    }

}

```

仅仅 @EnableEurekaClient 是不够的，还需要在配置文件中注明自己的服务注册中心的地址，application.yml 配置文件如下：

```
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
server:
  port: 8762
spring:
  application:
    name: service-hi

```

需要指明 spring.application.name, 这个很重要，这在以后的服务与服务之间相互调用一般都是根据这个 name 。
启动工程，打开 [http://localhost:8761](http://localhost:8761) ，即 eureka server 的网址：

![](http://upload-images.jianshu.io/upload_images/2279594-d830f93f1e56f6a2.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/600)

你会发现一个服务已经注册在服务中了，服务名为 SERVICE-HI , 端口为 7862

这时打开 [https://github.com/forezp/SpringCloudLearning/tree/master/chapter1](http://localhost:8762/hi?>http://localhost:8762/hi?name=forezp</a> ，你会在浏览器上看到 :</p>

<blockquote>
  <p>hi forezp,i am from port:8762</p>
</blockquote>


### 四、参考资料

[springcloud eureka server 官方文档](http://projects.spring.io/spring-cloud/spring-cloud.html#spring-cloud-eureka-server)

[springcloud eureka client 官方文档](http://projects.spring.io/spring-cloud/spring-cloud.html#_service_discovery_eureka_clients)

### 优秀文章推荐：

*   [史上最简单的 SpringCloud 教程 | 终章](http://blog.csdn.net/forezp/article/details/70148833)
*   [史上最简单的 SpringCloud 教程 | 第一篇: 服务的注册与发现（Eureka）](http://blog.csdn.net/forezp/article/details/69696915)
*   [史上最简单的 SpringCloud 教程 | 第七篇: 高可用的分布式配置中心 (Spring Cloud Config)](http://blog.csdn.net/forezp/article/details/70037513)

> 转载请标明出处：
> [http://blog.csdn.net/forezp/article/details/69696915](http://blog.csdn.net/forezp/article/details/69696915)
> 本文出自[方志朋的博客](http://blog.csdn.net/forezp)