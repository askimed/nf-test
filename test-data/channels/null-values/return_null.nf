process return_null {

    output:
    val null_list, emit: null_list

    exec:
    null_list = ["0", "1", "", null, "4"]
}