function (doc, meta) {
  if(doc.__type) {
   emit(doc.__type,null);
  }
}