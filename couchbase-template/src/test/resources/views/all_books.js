function (doc, meta) {
  if(meta.id.match("Book:")) (
   emit(meta.id, null)
  )
}