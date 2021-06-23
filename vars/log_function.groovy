def call(message, var)
{
 println(message)
 Date date = new Date() 
 sh " echo '${date}' ${var} ${message} >>log.txt"
}
