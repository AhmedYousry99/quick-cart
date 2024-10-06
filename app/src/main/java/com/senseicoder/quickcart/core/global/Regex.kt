package com.senseicoder.quickcart.core.global

/** Minimum six characters, at least one letter and one number:*/
val PasswordRegex = """^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{6,}$""".toRegex()

/**Minimum six characters, at least one letter, one number and one special character:*/
val regex2 = """^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{6,}$""".toRegex()

/**Minimum six characters, at least one uppercase letter, one lowercase letter and one number:*/
val regex3 = """^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{6,}$""".toRegex()

/**Minimum six characters, at least one uppercase letter, one lowercase letter, one number and one special character:*/
val regex4 = """^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{6,}$""".toRegex()

/**Minimum six and maximum 10 characters, at least one uppercase letter, one lowercase letter, one number and one special character:*/
val regex5 = """^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{6,10}$""".toRegex()