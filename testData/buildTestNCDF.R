require(raster)
require(ncdf4)

nRows = 16
nCols = 17

bool_var_names = c("boolPrim", "boolBox")
int_var_names = c("intPrim", "shortPrim", "longPrim", "bytePrim", 
                  "intBox", "shortBox", "longBox", "byteBox")
dbl_var_names = c("doublePrim", "floatPrim", "doubleBox", "floatBox")
chr_var_names = c("charprim", "charBox")
str_var_names = c("strng")


r_template = raster(nrows = nRows, ncols = nCols, xmn = 0, xmx = nCols, ymn = 0, ymx = nRows)
m_template = dat = matrix(nrow = nRows, ncol = nCols)

# boolean and Boolean
dat_r = r_template
dat_r[] = rep(c(-1, 1), (nRows * nCols) / 2)
names(dat_r) = "bl"
bric = brick(dat_r)

dat_r = r_template
dat_r[] = rep(c(1, -1), (nRows * nCols) / 2)
names(dat_r) = "blBoxed"
bric = addLayer(bric, dat_r)


for (v in 1:length(int_var_names))
{
  dat = matrix(nrow = nRows, ncol = nCols)
  for(i in seq(1, nRows))
  {
    dat[i, ] = seq(i + v, nCols + i + v - 1)
  }  
  dat_r = r_template
  dat_r[, ] = dat
  
  bric = addLayer(bric, dat_r)
}


for (v in 1:length(dbl_var_names))
{
  dat = matrix(nrow = nRows, ncol = nCols)
  for(i in seq(1, nRows))
  {
    dat[i, ] = (1 / i) * seq(i + v, nCols + i + v - 1)
  }  
  dat_r = r_template
  dat_r[, ] = dat
  bric = addLayer(bric, dat_r)
}

for (v in 1:length(chr_var_names))
{
  dat = matrix(nrow = nRows, ncol = nCols)
  for(i in seq(1, nRows))
  {
    dat[i, ] = seq(i + v, nCols + i + v - 1) + 60
  }  
  dat_r = r_template
  dat_r[, ] = dat
  bric = addLayer(bric, dat_r)
}


# string
dat_r = r_template
dat_r[] = rep(c(-111, 111), (nRows * nCols) / 2)
bric = addLayer(bric, dat_r)
# 
# bric
# # subset(bric, 13)[, ]
# names(bric) = c(bool_var_names, int_var_names, dbl_var_names, chr_var_names, "strng")
# 
# 
# # ncfname = "AllFlavorBean.nc"
# # writeRaster(bric, ncfname, "CDF", overwrite = T)
# 
# # dataType(bric)[i] = "INT4S"
# 
# # i = 1
# # dataType(subset(bric, i)) = "INT4S"
# 
# 
# 
# 
# c(bool_var_names, int_var_names, dbl_var_names, chr_var_names, "strng")

# NetCDF dimensions
x_dim = ncdim_def("x", "x", as.double(1:nCols))
y_dim = ncdim_def("y", "y", as.double(1:nRows))


dims = list(x_dim, y_dim)

# NetCDF variables
# 
#

# all_var_names = c(bool_var_names, int_var_names, dbl_var_names, chr_var_names, "strng")






var_list_1 =
  lapply(bool_var_names, function(vv) {ncvar_def(name = vv,      units = "none",  dim = dims, missval = -9999, prec = "integer")})
var_list_2 =
  lapply(int_var_names, function(vv) {ncvar_def(name = vv,      units = "none",  dim = dims, missval = -9999, prec = "integer")})
var_list_3 =
  lapply(chr_var_names, function(vv) {ncvar_def(name = vv,      units = "none",  dim = dims, missval = "N", prec = "char")})
var_list_4 =
  lapply(dbl_var_names, function(vv) {ncvar_def(name = vv,      units = "none",  dim = dims, missval = -9999, prec = "double")})
var_list_5 = list(ncvar_def(name = "strng", units = "none",  dim = dims, missval = "N", prec = "char"))


var_list = list()
list_index = 1
for (v in var_list_1) {var_list[[list_index]] = v; list_index = list_index + 1}
for (v in var_list_2) {var_list[[list_index]] = v; list_index = list_index + 1}
for (v in var_list_3) {var_list[[list_index]] = v; list_index = list_index + 1}
for (v in var_list_4) {var_list[[list_index]] = v; list_index = list_index + 1}
var_list[[list_index]] = var_list_5[[1]]

str(var_list_2, 0)
str(var_list, 0)





ncfname = "AllFlavorBean12.nc"
ncout <- nc_create(ncfname, var_list, force_v4 = T)

for (i in 1:length(var_list))
{
  ncvar_put(ncout, var_list[[i]], t(as.matrix(subset(bric, i))))
}
nc_close(ncout)



