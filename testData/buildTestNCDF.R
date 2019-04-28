require(raster)
require(ncdf4)

nRows = 16
nCols = 17

bool_varNames = c("bl", "blBoxed")
int_var_names = c("ii", "ss", "ll", "bt", 
                  "iiBoxed", "ssBoxed", "llBoxed", "btBoxed")
dbl_var_names = c("dd", "ff", "ddBoxed", "ffBoxed")
chr_var_names = c("cc", "ccBoxed")


r_template = raster(nrows = nRows, ncols = nCols, xmn = 0, xmx = nCols, ymn = 0, ymx = nRows)
m_template = dat = matrix(nrow = nRows, ncol = nCols)

# boolean and Boolean
dat_r = r_template
dat_r[] = rep(c(-1, 1), (nRows * nCols) / 2)
bric = brick(dat_r)

dat_r = r_template
dat_r[] = rep(c(1, -1), (nRows * nCols) / 2)
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

bric
# subset(bric, 13)[, ]



# NetCDF dimensions
x_dim = ncdim_def("x", "x", as.double(1:nCols))
y_dim = ncdim_def("y", "y", as.double(1:nRows))


dims = list(x_dim, y_dim)

# NetCDF variables
# 
#

bool_var_names = c("bl", "blBoxed")
all_var_names = c(bool_var_names, int_var_names, dbl_var_names, chr_var_names, "strng")

var_list = list()

for (vv in bool_var_names) var_list = c(var_list, 
ncvar_def(name = vv,      units = "none",  dim = dims, missval = NA, prec = "integer"))


bh_elevation_def      = ncvar_def(name = "elevation",      units = "meters",  dim = dims, missval = NA, prec = "double")
bh_slope_def          = ncvar_def(name = "slope",          units = "degrees", dim = dims, missval = NA, prec = "double")
bh_aspect_def         = ncvar_def(name = "aspect",         units = "degrees", dim = dims, missval = NA, prec = "double")
bh_dist_to_road_def   = ncvar_def(name = "dist_to_road",   units = "meters",  dim = dims, missval = NA, prec = "double")
bh_dist_to_stream_def = ncvar_def(name = "dist_to_stream", units = "meters",  dim = dims, missval = NA, prec = "double")
bh_border_def         = ncvar_def(name = "in_border",      units = "",      dim = dims, missval = 0, prec = "integer")
bh_road_def           = ncvar_def(name = "has_road",       units = "",      dim = dims, missval = 0, prec = "integer")
bh_stream_def         = ncvar_def(name = "has_stream",     units = "",      dim = dims, missval = 0, prec = "integer")

# create netCDF file and put arrays

ncout <- nc_create(ncfname, list(bh_elevation_def, bh_slope_def, bh_aspect_def, bh_dist_to_road_def, bh_dist_to_stream_def, bh_border_def, bh_road_def, bh_stream_def), force_v4=TRUE)

# Values
ncvar_put(ncout, bh_elevation_def,      t(as.matrix(bh_terrain$elevation)))
ncvar_put(ncout, bh_slope_def,          t(as.matrix(bh_terrain$slope)))
ncvar_put(ncout, bh_aspect_def,         t(as.matrix(bh_terrain$aspect)))
ncvar_put(ncout, bh_dist_to_road_def,   t(as.matrix(dist_to_road)))
ncvar_put(ncout, bh_dist_to_stream_def, t(as.matrix(dist_to_stream)))

