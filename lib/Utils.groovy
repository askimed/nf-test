
class Utils {

    public static void sayHello(name) {
        if (name == null) {
            error('Cannot greet a null person')
        }

        def greeting = "Hello ${name}"

        println(greeting)
    }

    def static uppercase(String name){
      return name.toUpperCase();
    }

}
