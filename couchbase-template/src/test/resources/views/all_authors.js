function (doc, meta) {
  if(meta.id.match("Author:")) (
   emit(meta.id, null)
  )
}