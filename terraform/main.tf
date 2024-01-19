terraform {
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "4.51.0"
    }
  }
}

provider "google" {
  credentials = file("../../keys/playpen-fbeec3.json")

  project = "playpen-fbeec3"
  region  = "europe-west2"
}

// Cloud Data Fusion Basic Instance

data "google_compute_default_service_account" "default" {
}
resource "google_data_fusion_instance" "basic_instance" {
  name                          = "schema-validation-demo"
  region                        = "europe-west2"
  type                          = "BASIC"
  version                       = "6.9.2"
  enable_stackdriver_logging    = true
  enable_stackdriver_monitoring = false
  private_instance              = false
  dataproc_service_account      = data.google_compute_default_service_account.default.email
  // Needs Cloud Data Fusion API Service Agent role and Editor
}

// Big Query Dataset & Table (Ingested files)
resource "google_bigquery_dataset" "input_dataset" {
  dataset_id = "input_dataset"
  location   = "europe-west2"
}
/*
resource "google_bigqery_table" "input-table" {
  dataset_id = google_bigquery_dataset.input-dataset.dataset_id
  table_id   = "ingested-data"
}
*/

// Big Query Dataset & Table (Schemas)
resource "google_bigquery_dataset" "schema_dataset" {
  dataset_id = "schema_dataset"
  location   = "europe-west2"
}
/*
resource "google_bigquery_table" "schema-table" {
  dataset_id = google_bigquery_dataset.schema-dataset.dataset_id
  table_id   = "schemas"
}
*/

// Big Query Dataset & Table (Outputted files)
resource "google_bigquery_dataset" "output_dataset" {
  dataset_id = "output_dataset"
  location   = "europe-west2"
}
/*
resource "google_bigquery_table" "output-table" {
  dataset_id = google_bigquery_dataset.output-dataset.dataset_id
  table_id   = "outputted-data"
}
*/