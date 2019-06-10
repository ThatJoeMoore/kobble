# Kobble

Kobble is an experimental to cobble together Kotlin/JVM server applications 
(and, perhaps, for Android, Native, and Web apps too, if we like it).

This is just an exploration of an idea, and you shouldn't count on any kind of support or maintenance.

# Goals

* Low-magic helpers to make writing your own DI/IoC modules almost as easy as something like Kodein or Koin
* Static scopes (singletons, factories, multitons, prototypes, etc)
* Multi-module with explicit imports
* Enforce visibility between modules (only things the module says are public are accessible by anybody else)
* Explicit dependency trees (avoid generic 'gimme something with this interface' most of the time)

Nice to have: 
* Dynamic scopes (request, etc)
* Dynamic resolution (gimme something with this interface)
* Sets (gimme everything with this interface)
* Auto-closing of closeable resources

If it's not terribly ugly/difficult:
* Constructor Injection
* JSR 330 support (`@Inject`)

Probably not going to have:
* Completely dynamic resolution
* Runtime component substitution (just instantiate a new module with different params!)

# Initial Sketch

```kotlin

import kobble.*

class MyModule(
  private val isProd: Boolean
): KobbleModule() {
 
    // This is something we export!
    val fooRepo: FooRepository by singleton { SqlFooRepository(this.datasources.dataSource) }
    
    // Just use variables! Also, this singleton is eager - things are lazy by default
    val appName: String by singleton(eager = true) { "MyApp - ${if (isProd) "prod" else "test"}" }
    
    // This is a factory
    fun fooFactory(name: String): Foo = factory { name ->
        buildAFoo(name)
    }
    
    // alternative?
    val barFactory: (String) -> Bar by factory { name: String ->
        buildABar(name)
    }
   
    // Imports are usually not exported
    private val datasources: DatasourceModule by imported { 
        if (isProd) {
            MyProdDatasourceModule()
        } else {
            MyTestDatasourceModule()
        }
    }
    
    private val parameterizedModule by imported {
        MyParameterizedModule(datasources, appName)
    }
    
    val dynamicConstruction by singleton { Gizmo(dynamic<DataSource>()) }
    
    val injectable by injectedSingleton<Widget>()
}
 
interface DatasourceModule {
    val dataSource: DataSource
}

class MyProdDatasourceModule: KobbleModule(), DatasourceModule {
    override val dataSource: DataSource by kobble.singleton { connectToRealDb() }
}

class MyTestDatasourceModule: KobbleModule(), DatasourceModule {
    override val dataSource: DataSource by kobble.singleton { connectToTestDb() }
}

class MyParameterizedModule(
    private val datasources: DatasourceModule,
    private val appName: String
): KobbleModule() {
    val baz: Baz by singleton { Baz(datasources.dataSource, appName) }
}

```
