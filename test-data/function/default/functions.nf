def say_hello(name) {
    if (name == null) {
        error('Cannot greet a null person')
    }
        
    def greeting = "Hello ${name}"

    println(greeting)
    return greeting
}