% script calcola_soluzione_aggre 
%
% this script solves the optimization problem. The optimal solution, if the problem is feasible,
% or the string 'Soluzione non trovata' (Solution not found) is written to the file 'soluzione_provider'; 
% The composition of the redundancy groups variables is then written to the file 'gruppi_J'

[X,fval,lin_prog_flag]=linprog(costo,A,B,Aeq,Beq,zeros(nvar,1),[],[]);

for indice=1:4
   for k=1:K
     ris(k,indice)=calcola_indice_aggre(X,N,xcode,base,q,k,indice,gamma,la_medio,0,[],[]);      
   end
end
ris(:,4)=exp(ris(:,4));


f=fopen('soluzione_provider','w');

if lin_prog_flag==1

   scrivi_mat=zeros(contaJ+contaJ1,K+1);

   scrivi_mat(:,1)=[(1:contaJ) (1:contaJ1)]' ;

   for k=1:K
       indj=0;
       for n=1:N  
           if ~node(n).tipo
               app=[];
               for J=1:length(node(n).sets)
                   indj=indj+1;
                   ind=find(xcode==ind_to_code([n J k 0],base));
                   scrivi_mat(indj,k+1)=X(ind);
               end
           end
       end
   end

   indsf=indj;

   for k=1:K
       indj=0;
       for n=1:N  
           if ~node(n).tipo
               app=[];
               for J1=1:length(node(n).sets1)
                   indj=indj+1;
                   ind=find(xcode==ind_to_code([n J1 k 4],base));
                   scrivi_mat(indj+indsf,k+1)=X(ind);
               end
           end
       end
   end

   for i=1:(contaJ+contaJ1)
       fprintf(f,' %10f ', scrivi_mat(i,:));
       fprintf(f,' \n');
   end
else
   fprintf(f,'Soluzione non trovata\n'); 
end


fclose(f);



f=fopen('gruppi_J','w');

scrivi_mat=zeros(contaJ+contaJ1,maxS+1);

scrivi_mat(:,1)=[(1:contaJ) (1:contaJ1)]' ;

indj=0;


for n=1:N  
   if ~node(n).tipo
       app=[];
       for J=1:length(node(n).sets)
           indj=indj+1;
           ind=find(xcode==ind_to_code([n J k 0],base));
           scrivi_mat(indj,2:(length(node(n).sets{J})+2))=[node(n).abstract node(n).sets{J}];
       end
   end
end

indsf=indj;

indj=0;

	for n=1:N  
       if ~node(n).tipo
           app=[];
           for J1=1:length(node(n).sets1)
               indj=indj+1;
               ind=find(xcode==ind_to_code([n J1 k 4],base));
               scrivi_mat(indj+indsf,2:(length(node(n).sets1{J1})+2))=[ node(n).abstract node(n).sets1{J1}];
           end
       end
	end


for i=1:(contaJ+contaJ1)
   fprintf(f,' %10f ', scrivi_mat(i,:));
   fprintf(f,' \n');
end

fclose(f);