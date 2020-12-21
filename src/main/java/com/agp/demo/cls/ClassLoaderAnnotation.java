package com.agp.demo.cls;

/**
 * The <tt>ClassLoader</tt> class uses a delegation model to search for
 *  * classes and resources.  Each instance of <tt>ClassLoader</tt> has an
 *  * associated parent class loader.  When requested to find a class or
 *  * resource, a <tt>ClassLoader</tt> instance will delegate the search for the
 *  * class or resource to its parent class loader before attempting to find the
 *  * class or resource itself.
 *  双亲委派机制
 *虚拟机内置的classLoader是 bootstrap class Loader。
 *
 *The virtual machine's built-in class loader,
 *  * called the "bootstrap class loader",
 *
 *
 *  通常加载本地CLASSPATH下的类文件。
 *
 *  Normally, the Java virtual machine loads classes from the local file
 *  * system in a platform-dependent manner.
 *
 *For example, on UNIX systems, the
 *  * virtual machine loads classes from the directory defined by the
 *  * <tt>CLASSPATH</tt> environment variable.
 *
 *但是也可以从其他源加载类，比如network。 可以被应用加载。
 * defineClass(String,byte[],int,int)方法可以将字节数组转化为类
 *
 *  However, some classes may not originate from a file; they may originate
 *  * from other sources, such as the network, or they could be constructed by an
 *  * application.
 *
 *The method {@link #defineClass(String, byte[], int, int)
 *  * <tt>defineClass</tt>} converts an array of bytes into an instance of class
 *  * <tt>Class</tt>.
 *
 *
 *
 *   * <p> The network class loader subclass must define the methods {@link
 *  * #findClass <tt>findClass</tt>} and <tt>loadClassData</tt> to load a class
 *  * from the network.
 *
 *
 *
 *  Once it has downloaded the bytes that make up the class,
 *  * it should use the method {@link #defineClass <tt>defineClass</tt>} to
 *  * create a class instance.  A sample implementation is:
 *
 *  *     class NetworkClassLoader extends ClassLoader {
 *  *         String host;
 *  *         int port;
 *  *
 *  *         public Class findClass(String name) {
 *  *             byte[] b = loadClassData(name);
 *  *             return defineClass(name, b, 0, b.length);
 *  *         }
 *  *
 *  *         private byte[] loadClassData(String name) {
 *  *             // load the class data from the connection
 *  *             &nbsp;.&nbsp;.&nbsp;.
 *  *         }
 *  *     }
 *
 *
 *  自定义子类加载器还需要调用  defineClass，将字节数组生成Class
 *  Converts an array of bytes into an instance of class <tt>Class</tt>
 *  protected final Class<?> defineClass(byte[] b, int off, int len)
 *
 */
public class ClassLoaderAnnotation {
}
