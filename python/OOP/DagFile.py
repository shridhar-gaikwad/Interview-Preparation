from airflow import DAG
from airflow.operators.python import PythonOperator
from datetime import datetime

def extract():
    print("Extracting data")

def transform():
    print("Transforming data")

def load():
    print("Loading data")

with DAG(
        dag_id="customer_data_pipeline",
        start_date=datetime(2025, 1, 1),
        schedule="@daily",
        catchup=False
) as dag:

    task1 = PythonOperator(
        task_id="extract",
        python_callable=extract
    )

    task2 = PythonOperator(
        task_id="transform",
        python_callable=transform
    )

    task3 = PythonOperator(
        task_id="load",
        python_callable=load
    )

    task1 >> task2 >> task3