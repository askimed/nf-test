process SAY_HELLO {

  input:
    val name
    val number

  output:
     tuple val(number), val(name), emit: my_tuples
 
  script:
    if (number < 0) {
      error "Negative numbers not allowed"
    }
 
  """
  echo "Hi there! I am ${name} number ${number}" > "${number}_${name}.txt"
  """

}
