function (doc, meta) {
  if(meta.id.match("Book:")) (
   emit(doc.author, null)
  )
}