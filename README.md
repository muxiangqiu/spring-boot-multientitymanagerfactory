## 简介
在单数据源项目中，spring-boot提供属性文件配置的方式，配置我们的数据源，如下：
```java
#数据源配置
spring.dataSource......
#数据源对应的JPA配置
#spirng.jpa......
```
如果项目需要多个数据源的话，spring-boot就没有更好的方式了，只能通过自己构造DataSource和EntityManagerFactory的spring bean了。
## 多数据源配置
1. 单数据源情况和spring boot提供的方式是一样的。无须额外的学习
2. 多数据源和单数据源配置方式差不多，代码如下：
```java
spring.dataSource.........
spring.dataSource1........
spring.dataSource2........
....
spring.dataSource6........

spring.jpa.........
spring.jpa1........
spring.jpa2........
....
spring.jpa6........
```
代码上可以看出，配置方式和单数据源方式几乎一样，需要特别主要的是：这种通过属性配置来定义多数据源的数据源的个数是有限制的，最多7个数据源。这对绝大多数应用场景是远远足够了的。万一不够，我们还可以spring原生的方式：构建自己的DataSource和EntityManagerFactory对象放入spring容器中，效果也是一样的。
## 如何切换和使用多数据
在绝大数场景中，只要使用linq，开发人员不需要关心数据源的切换，linq已经做到了多数据切换对开发人员透明。
### 自动切换数据源原理
EntityManagerFactory在项目启动时，会进行实体类元数据的收集，包含实体类方方面面信息。linq在启动的时候会收集所有的EntityManagerFactory的bean，当我们使用JpaUtil对实体类对象进行数据库操作时，linq首先会取到对象的实体类型，然后根据实体类型与收集的EntityManagerFactory进行匹配，匹配成功则返回给EntityManagerFactory的当前线程关联的EntityManager（无论是增删改还是查询操作都需要加spring的事务注解，当然，查询的话，只需要加只读事务，不加，则会报空指针异常，因为找不到EntityManger）。
## 当某个实体类同时属于多个EntityManagerFactory
在linq中，绝大部分情况，一个实体类只会属于一个EntityManagerFactory，我们要尽量避免实体类同时属于多个EntityManagerFactory，但是，当出现这种情况时，如果使用默认的匹配方式，就会随机匹配一个EntityManagerFactory，这样往往不是我们想要的结果。这种情况下，bdf3提供了很多数据操作方法的重载方法，用开发人员自己获取的EntityManager。另外，可以实现GetEntityManagerFactoryStrategy接口，自定义获取EntityManagerFactory策略。
